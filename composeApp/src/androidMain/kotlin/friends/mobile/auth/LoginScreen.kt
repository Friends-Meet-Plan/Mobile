package friends.mobile.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.presentation.login.LoginAction
import friends.mobile.feature.auth.presentation.login.LoginEvent
import friends.mobile.feature.auth.presentation.login.LoginViewModel
import friends.mobile.feature.auth.presentation.login.LoginViewState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (AuthSession) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    var showRegister by remember { mutableStateOf(false) }
    var errorAlertMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is LoginAction.NavigateToHome -> onLoginSuccess(action.session)
                is LoginAction.ShowMessage -> {
                    errorAlertMessage = action.message
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = state) {
            is LoginViewState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is LoginViewState.Error -> {
                // Если стейт все же перешел в Error (например, фатальная ошибка), 
                // мы просто покажем форму, но с сообщением об ошибке в алерте.
                // Чтобы пользователь не видел пустой экран.
                LaunchedEffect(currentState) {
                    errorAlertMessage = currentState.message
                }
                LoginForm(
                    state = LoginViewState.Content(),
                    onEvent = viewModel::obtainEvent,
                    onRegisterClick = { showRegister = true }
                )
            }
            is LoginViewState.Content -> {
                LoginForm(
                    state = currentState,
                    onEvent = viewModel::obtainEvent,
                    onRegisterClick = { showRegister = true }
                )
            }
        }
    }

    // Показываем алерт вместо нового экрана
    errorAlertMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorAlertMessage = null },
            title = { Text("Authentication Error") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { errorAlertMessage = null }) {
                    Text("OK")
                }
            }
        )
    }

    if (showRegister) {
        RegisterBottomSheet(
            onDismiss = { showRegister = false },
            onRegisterSuccess = { showRegister = false }
        )
    }
}

@Composable
private fun LoginForm(
    state: LoginViewState.Content,
    onEvent: (LoginEvent) -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome Back", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = { onEvent(LoginEvent.OnUsernameChanged(it)) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onEvent(LoginEvent.OnLoginClick) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !state.isLoggingIn && state.username.isNotBlank() && state.password.isNotBlank()
        ) {
            if (state.isLoggingIn) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Register",
            modifier = Modifier.clickable { onRegisterClick() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
