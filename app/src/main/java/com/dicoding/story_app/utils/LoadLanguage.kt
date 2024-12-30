package com.dicoding.story_app.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun loadLanguagePreference(context: Context) {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val language = sharedPreferences.getString("language", "English")
    val locale = when (language) {
        "Bahasa Indonesia" -> Locale("id")
        else -> Locale("en")
    }
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.createConfigurationContext(config)
}