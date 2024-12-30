package com.dicoding.story_app.di

import android.content.Context
import com.dicoding.story_app.data.database.StoryDatabase
import com.dicoding.story_app.data.perf.UserPreference
import com.dicoding.story_app.data.perf.dataStore
import com.dicoding.story_app.data.repository.UserRepository
import com.dicoding.story_app.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        return UserRepository.getInstance(apiService, pref, database)
    }
}