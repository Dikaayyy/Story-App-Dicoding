package com.dicoding.story_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dicoding.story_app.data.database.StoryDatabase
import com.dicoding.story_app.data.perf.UserPreference
import com.dicoding.story_app.data.perf.dataStore
import com.dicoding.story_app.navigation.NavGraph
import com.dicoding.story_app.network.ApiConfig
import com.dicoding.story_app.ui.theme.StoryAppTheme
import com.dicoding.story_app.utils.loadLanguagePreference

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreference = UserPreference.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        loadLanguagePreference(this)
        setContent {
            StoryAppTheme {
                val database = StoryDatabase.getDatabase(this)
                NavGraph(userPreference = userPreference, apiService = apiService, database = database)
            }
        }
    }
}