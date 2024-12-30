package com.dicoding.story_app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.story_app.data.database.StoryDatabase
import com.dicoding.story_app.data.database.StoryRemoteMediator
import com.dicoding.story_app.data.perf.UserModel
import com.dicoding.story_app.data.perf.UserPreference
import com.dicoding.story_app.data.response.LoginResponse
import com.dicoding.story_app.data.response.RegisterResponse
import com.dicoding.story_app.data.response.Story
import com.dicoding.story_app.data.response.StoryDetailResponse
import com.dicoding.story_app.data.response.StoryResponse
import com.dicoding.story_app.network.ApiService
import com.dicoding.story_app.utils.uriToFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun isLoggedIn(): Boolean {
        val token = userPreference.getToken().first()
        return token.isNotEmpty()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Response<RegisterResponse> {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiService.login(email, password)
    }

    suspend fun getStoriesWithLocation(): Result<StoryResponse> {
        return try {
            val token = userPreference.getToken().first()
            val response = apiService.getStoriesWithLocation("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getStoryDetail(storyId: String): StoryDetailResponse {
        return try {
            val token = userPreference.getToken().first()
            val response = apiService.getStoryDetail("Bearer $token", storyId)
            if (response.isSuccessful) {
                response.body() ?: throw IllegalStateException("Response body is null")
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun uploadStory(
        context: Context,
        description: String,
        imageUri: Uri,
        latitude: Double?,
        longitude: Double?
    ): Boolean {
        return try {
            val token = userPreference.getToken().first()
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val file = uriToFile(context, imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            val latitudePart = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudePart =
                longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadStory(
                "Bearer $token",
                descriptionPart,
                body,
                latitudePart,
                longitudePart
            )

            Log.d("UploadStory", "Response: ${response.body()}")

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("UploadStory", "Error: ${e.message}")
            false
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStory(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                apiService,
                userPreference
            )
        ) {
            storyDatabase.StoryDao().getAllStory()
        }.flow
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(
                    apiService,
                    userPreference,
                    storyDatabase
                ).also { instance = it }
            }
    }
}