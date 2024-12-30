package com.dicoding.story_app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.story_app.data.repository.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _signupResult = MutableLiveData<String?>()
    val signupResult: LiveData<String?> get() = _signupResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repository.register(name, email, password)
                _signupResult.value =
                    if (result.isSuccessful) "Signup successful" else "Signup failed: ${
                        result.errorBody()?.string()
                    }"
            } catch (e: Exception) {
                _signupResult.value = "Signup failed: ${e.message}"
            }
        }
    }
}