package com.dicoding.story_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.story_app.data.repository.UserRepository
import com.dicoding.story_app.data.response.StoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _storiesWithLocation = MutableStateFlow(Result.success(StoryResponse()))
    val storiesWithLocation: StateFlow<Result<StoryResponse>> = _storiesWithLocation

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            _storiesWithLocation.value = userRepository.getStoriesWithLocation()
        }
    }
}