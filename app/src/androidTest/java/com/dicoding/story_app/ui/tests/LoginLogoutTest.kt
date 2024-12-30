package com.dicoding.story_app.ui.tests

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.story_app.data.database.StoryDatabase
import com.dicoding.story_app.data.perf.UserPreference
import com.dicoding.story_app.data.perf.dataStore
import com.dicoding.story_app.navigation.NavGraph
import com.dicoding.story_app.network.ApiConfig
import com.dicoding.story_app.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginLogoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

        @Test
        fun loginlogout() {
            composeTestRule.setContent {
                rememberNavController()
                val context = LocalContext.current
                val userPreference = UserPreference.getInstance(context.dataStore)
                val apiService = ApiConfig.getApiService()
                val database = StoryDatabase.getDatabase(context)
                NavGraph(
                    userPreference = userPreference,
                    apiService = apiService,
                    database = database
                )
            }

            composeTestRule.onNodeWithTag("Login").performClick()
            composeTestRule.onNodeWithTag("EmailInput").assertExists()

            // Perform login
            val email = "adika@gmail.com"
            val password = "adika123"
            composeTestRule.onNodeWithTag("EmailInput").performTextInput(email)
            composeTestRule.onNodeWithTag("PasswordInput").performTextInput(password)
            composeTestRule.onNodeWithTag("LoginButton").performClick()

            // Verify navigation to StoryListScreen
            composeTestRule.onNodeWithTag("stories").assertExists()

            // Navigate to Settings
            composeTestRule.onNodeWithTag("settings").performClick()
            composeTestRule.onNodeWithTag("LogoutButton").assertExists()

            // Perform logout
            composeTestRule.onNodeWithTag("LogoutButton").performClick()

            // Verify navigation back to Welcome screen
            composeTestRule.onNodeWithTag("Login").assertExists()
        }
    }