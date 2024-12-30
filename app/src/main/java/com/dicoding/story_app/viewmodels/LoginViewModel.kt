package com.dicoding.story_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.story_app.data.perf.UserModel
import com.dicoding.story_app.data.repository.UserRepository
import com.dicoding.story_app.data.response.ErrorResponse
import com.dicoding.story_app.utils.EspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = repository.isLoggedIn()
        }
    }

    fun login(email: String, password: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            EspressoIdlingResource.increment()
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult
                        ?: throw IllegalStateException("Login result is null")
                    val token = loginResult.token ?: throw IllegalStateException("Token is null")
                    val userModel = UserModel(loginResult.name ?: "", email, password, token, true)
                    repository.saveSession(userModel)
                    onResult("Login successful")
                } else {
                    onResult(response.message() ?: "Unknown error")
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                onResult(errorBody.message ?: "Unknown error")
            } catch (e: Exception) {
                onResult(e.message ?: "Unknown error")
            } finally {
                EspressoIdlingResource.decrement()
            }
        }
    }
}