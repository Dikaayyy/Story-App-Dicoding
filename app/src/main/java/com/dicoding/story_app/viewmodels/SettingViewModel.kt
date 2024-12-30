package com.dicoding.story_app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.story_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SettingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _navigateToWelcomeScreen = MutableStateFlow(false)
    val navigateToWelcomeScreen: StateFlow<Boolean> = _navigateToWelcomeScreen

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _navigateToWelcomeScreen.value = true
        }
    }

    fun setLanguage(language: String, context: Context) {
        val locale = when (language) {
            "Bahasa Indonesia" -> Locale("id")
            else -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("language", language)
            apply()
        }
    }

    fun loadLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("language", "English") ?: "English"
    }
}