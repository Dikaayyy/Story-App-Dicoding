package com.dicoding.story_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.story_app.data.repository.UserRepository
import com.dicoding.story_app.data.response.StoryDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryDetailViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _storyDetail = MutableStateFlow<StoryDetailResponse?>(null)
    val storyDetail: StateFlow<StoryDetailResponse?> = _storyDetail

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchStoryDetail(storyId: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getStoryDetail(storyId)
                _storyDetail.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = getUserFriendlyErrorMessage(e)
            }
        }
    }

    private fun getUserFriendlyErrorMessage(exception: Exception): String {
        return when (exception) {
            is java.net.UnknownHostException -> "No internet connection. Please check your network settings."
            is retrofit2.HttpException -> "Server error. Please try again later."
            else -> "An unexpected error occurred. Please try again."
        }
    }
}