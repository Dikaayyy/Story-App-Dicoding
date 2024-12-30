package com.dicoding.story_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.story_app.data.repository.UserRepository
import com.dicoding.story_app.data.response.Story
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest


class StoryViewModel(
    private val userRepository: UserRepository,
    private val externalScope: CoroutineScope? = null
) : ViewModel() {
    private val _refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val stories: Flow<PagingData<Story>> = _refreshTrigger
        .flatMapLatest {
            userRepository.getAllStory()
        }
        .cachedIn(externalScope ?: viewModelScope)
    fun refresh() {
        _refreshTrigger.value += 1
    }
}