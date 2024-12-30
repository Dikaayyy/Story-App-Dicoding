package com.dicoding.story_app.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.story_app.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    fun uploadStory(
        context: Context,
        description: String,
        imageUri: Uri?,
        latitude: Double?,
        longitude: Double?,
        onResult: (Boolean) -> Unit,
        onUploadSuccess: () -> Unit

    ) {
        if (imageUri == null) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            try {
                Log.d("AddStoryViewModel", "Latitude: $latitude, Longitude: $longitude")

                val result = repository.uploadStory(context, description, imageUri, latitude, longitude)
                if (result) {
                    onResult(true)  // First notify about success
                    delay(300) // Add small delay to ensure database transaction is complete
                    onUploadSuccess() // Then trigger refresh
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("AddStoryViewModel", "Error: ${e.message}")
                onResult(false)
            }
        }
    }
}