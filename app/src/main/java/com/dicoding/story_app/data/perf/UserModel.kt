package com.dicoding.story_app.data.perf

data class UserModel(
    val name: String,
    val email: String,
    val password: String,
    val token: String,
    val isLogin: Boolean = false
)