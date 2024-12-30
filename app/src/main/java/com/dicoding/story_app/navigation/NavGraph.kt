package com.dicoding.story_app.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dicoding.story_app.data.database.StoryDatabase
import com.dicoding.story_app.data.perf.UserPreference
import com.dicoding.story_app.data.repository.UserRepository
import com.dicoding.story_app.network.ApiService
import com.dicoding.story_app.ui.screens.addstory.AddStoryScreen
import com.dicoding.story_app.ui.screens.login.LoginScreen
import com.dicoding.story_app.ui.screens.map.MapScreen
import com.dicoding.story_app.ui.screens.setting.SettingScreen
import com.dicoding.story_app.ui.screens.signup.SignupScreen
import com.dicoding.story_app.ui.screens.story.StoryDetailScreen
import com.dicoding.story_app.ui.screens.story.StoryListScreen
import com.dicoding.story_app.ui.screens.welcome.WelcomeScreen
import com.dicoding.story_app.viewmodels.LoginViewModel
import com.dicoding.story_app.viewmodels.ViewModelFactory
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(userPreference: UserPreference, apiService: ApiService, database: StoryDatabase,
             ) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val loginViewModel: LoginViewModel = viewModel(
        factory = ViewModelFactory(
            UserRepository.getInstance(
                apiService,
                userPreference,
                database,
            )
        )
    )
    val isLoggedInState = loginViewModel.isLoggedIn.collectAsState()
    val isLoggedIn = isLoggedInState.value

    LaunchedEffect(isLoggedIn) {
        coroutineScope.launch {
            if (isLoggedIn) {
                navController.navigate(Routes.STORY_SCREEN) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.navigate(Routes.WELCOME_SCREEN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.STORY_SCREEN else Routes.WELCOME_SCREEN
    ) {
        composable(
            route = Routes.WELCOME_SCREEN
        ) {
            WelcomeScreen(navController)
        }

        composable(
            route = Routes.LOGIN_SCREEN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            LoginScreen(navController)
        }


        composable(
            route = Routes.SIGNUP_SCREEN,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            SignupScreen(navController)
        }

        composable(
            route = Routes.STORY_SCREEN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            StoryListScreen(navController)
        }

        composable(
            route = Routes.STORY_DETAIL_SCREEN,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType }),
            enterTransition = {
                fadeIn(animationSpec = tween(500)) +
                        expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = tween(500)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500)) +
                        shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            animationSpec = tween(500)
                        )
            }
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId")
            storyId?.let {
                StoryDetailScreen(it, navController)
            }
        }

        composable(
            route = Routes.ADD_STORY_SCREEN,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            AddStoryScreen(navController)
        }
        composable(
            route = Routes.SETTINGS_SCREEN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            SettingScreen(navController)
        }
        composable(
            route = Routes.MAP_SCREEN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            MapScreen()
        }
    }
}