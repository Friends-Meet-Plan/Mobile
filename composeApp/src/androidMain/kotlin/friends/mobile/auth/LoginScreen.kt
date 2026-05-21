package friends.mobile.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import friends.mobile.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designkit.DesignTheme
import friends.mobile.designkit.FormErrorMessage
import friends.mobile.designkit.FormSecureField
import friends.mobile.designkit.FormTextField
import friends.mobile.designkit.PrimaryButton
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DesignTheme.Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            vertical = DesignTheme.Spacing.lg
        )
    ) {
        item {
            Spacer(modifier = Modifier.height(DesignTheme.Spacing.md))
        }

        item {
            Image(
                painter = painterResource(id = R.drawable.onboarding_image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = DesignTheme.Spacing.sm)
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FormTextField(
                    value = state.username,
                    onValueChange = { onEvent(LoginEvent.OnUsernameChanged(it)) },
                    placeholder = "Username",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false
                    )
                )

                FormSecureField(
                    value = state.password,
                    onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) },
                    placeholder = "Password",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        }

        item {
            PrimaryButton(
                text = if (state.isLoggingIn) "Logging in..." else "Login",
                onClick = { onEvent(LoginEvent.OnLoginClick) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoggingIn,
                isEnabled = state.username.isNotBlank() && state.password.isNotBlank()
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account?",
                    style = DesignTheme.Typography.caption,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(DesignTheme.Spacing.xs))
                Text(
                    text = "Sign up",
                    modifier = Modifier.clickable { onRegisterClick() },
                    style = DesignTheme.Typography.captionSemibold,
                    color = DesignTheme.Colors.primaryHex
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(DesignTheme.Spacing.xl))
        }
    }
}
