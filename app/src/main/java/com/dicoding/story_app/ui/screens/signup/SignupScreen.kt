package com.dicoding.story_app.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dicoding.story_app.R
import com.dicoding.story_app.di.Injection
import com.dicoding.story_app.navigation.Routes
import com.dicoding.story_app.ui.components.AnimatedImage
import com.dicoding.story_app.ui.components.CustomEmailField
import com.dicoding.story_app.ui.components.CustomPasswordField
import com.dicoding.story_app.ui.theme.StoryAppTheme
import com.dicoding.story_app.viewmodels.SignupViewModel
import com.dicoding.story_app.viewmodels.ViewModelFactory

@Composable
fun SignupScreen(
    navController: NavController,
    viewModel: SignupViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(
                LocalContext.current
            )
        )
    )
) {
    StoryAppTheme {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }
        var dialogMessage by remember { mutableStateOf("") }
        var isSuccess by remember { mutableStateOf(false) }
        val signupResult by viewModel.signupResult.observeAsState()

        val signupSuccessMessage = stringResource(id = R.string.signup_success)
        val signupFailedMessage = stringResource(id = R.string.signup_failed)
        val successTitle = stringResource(id = R.string.success)
        val failedTitle = stringResource(id = R.string.failed)
        val okText = stringResource(id = R.string.ok)

        LaunchedEffect(signupResult) {
            signupResult?.let { message ->
                isLoading = false
                isSuccess = message == "Signup successful"
                dialogMessage = if (isSuccess) signupSuccessMessage else signupFailedMessage
                showDialog = true
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        AnimatedImage(
                            painter = painterResource(id = R.drawable.image_signup),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(id = R.string.signup),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(0.9f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = {
                            Text(
                                text = stringResource(id = R.string.name),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_person_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.9f),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomEmailField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.9f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.9f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            viewModel.register(name, email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading && name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.signup),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.dialog_login),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        TextButton(
                            onClick = {
                                navController.navigate(Routes.LOGIN_SCREEN) {
                                    popUpTo(Routes.SIGNUP_SCREEN) { inclusive = true }
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.login),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    if (isSuccess) {
                        navController.navigate(Routes.WELCOME_SCREEN) {
                            popUpTo(Routes.SIGNUP_SCREEN) { inclusive = true }
                        }
                    }
                },
                title = {
                    Text(
                        text = if (isSuccess) successTitle else failedTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSuccess) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = dialogMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            if (isSuccess) {
                                navController.navigate(Routes.WELCOME_SCREEN) {
                                    popUpTo(Routes.SIGNUP_SCREEN) { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text(
                            text = okText,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            )
        }
    }
}