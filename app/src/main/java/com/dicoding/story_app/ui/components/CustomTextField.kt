package com.dicoding.story_app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.dicoding.story_app.R

@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(id = R.string.password)
    val passwordError = stringResource(id = R.string.password_error)

    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            isError = it.length < 8
            errorMessage = if (isError) passwordError else ""
        },
        label = { Text(label) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_lock_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        isError = isError,
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier
            .fillMaxWidth()
            .alpha(1f)
    )
    if (isError) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CustomEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(id = R.string.email)
    val emailError = stringResource(id = R.string.email_error)

    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            isError = !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
            errorMessage = if (isError) emailError else ""
        },
        label = { Text(label) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_email_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        isError = isError,
        modifier = modifier
            .fillMaxWidth()
            .alpha(1f)
    )
    if (isError) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}