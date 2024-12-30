package com.dicoding.story_app.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dicoding.story_app.R
import com.dicoding.story_app.di.Injection
import com.dicoding.story_app.viewmodels.SettingViewModel
import com.dicoding.story_app.viewmodels.ViewModelFactory
import kotlinx.coroutines.delay
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    settingViewModel: SettingViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(
                LocalContext.current
            )
        )
    )
) {
    val navigateToWelcomeScreen by settingViewModel.navigateToWelcomeScreen.collectAsState()
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Bahasa Indonesia")
    var selectedLanguage by remember { mutableStateOf(settingViewModel.loadLanguage(context)) }
    var currentLanguage by remember { mutableStateOf(selectedLanguage) }

    var refreshKey by remember { mutableIntStateOf(0) }

    if (navigateToWelcomeScreen) {
        LaunchedEffect(Unit) {
            navController.navigate("welcome_screen") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(currentLanguage) {
        selectedLanguage = settingViewModel.loadLanguage(context)
    }

    fun updateLanguageAndRefresh(newLanguage: String) {
        selectedLanguage = newLanguage
        expanded = false
        settingViewModel.setLanguage(newLanguage, context)
        currentLanguage = newLanguage
        refreshKey += 1
    }

    LaunchedEffect(refreshKey) {
        delay(100)
        // Trigger recomposition or any other side effect
    }

    key(refreshKey) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.settings),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Language Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = stringResource(id = R.string.select_language),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(id = R.string.select_language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = currentLanguage)
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                languages.forEach { language ->
                                    DropdownMenuItem(
                                        onClick = { updateLanguageAndRefresh(language) },
                                        text = { Text(text = language) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { settingViewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("LogoutButton"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(id = R.string.logout)
                        )
                        Text(
                            text = stringResource(id = R.string.logout),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}