package friends.mobile.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.R
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.ButtonFactory
import friends.mobile.designkit.components.FormErrorMessage
import friends.mobile.designkit.components.FormSecureField
import friends.mobile.designkit.components.FormTextField
import friends.mobile.designkit.components.LoadingView
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
    var sideEffectError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is LoginAction.NavigateToHome -> onLoginSuccess(action.session)
                is LoginAction.ShowMessage -> sideEffectError = action.message
            }
        }
    }

    when (val currentState = state) {
        is LoginViewState.Loading -> {
            LoadingView(message = "Signing in...")
        }
        is LoginViewState.Error -> {
            LoginForm(
                state = LoginViewState.Content(),
                errorMessage = currentState.message,
                onEvent = viewModel::obtainEvent,
                onRegisterClick = { showRegister = true }
            )
        }
        is LoginViewState.Content -> {
            LoginForm(
                state = currentState,
                errorMessage = sideEffectError,
                onEvent = { event ->
                    sideEffectError = null
                    viewModel.obtainEvent(event)
                },
                onRegisterClick = { showRegister = true }
            )
        }
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
    errorMessage: String?,
    onEvent: (LoginEvent) -> Unit,
    onRegisterClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            vertical = DesignTheme.Spacing.lg
        )
    ) {
        item {
            Spacer(modifier = Modifier.height(DesignTheme.Spacing.sm))
        }

        item {
            Image(
                painter = painterResource(id = R.drawable.onboarding_image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = DesignTheme.Spacing.sm, start = DesignTheme.Spacing.sm, end = DesignTheme.Spacing.sm)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.xl),
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

                if (errorMessage != null) {
                    FormErrorMessage(message = errorMessage)
                }
            }
        }

        item {
            ButtonFactory.primary(
                text = "Login",
                onClick = { onEvent(LoginEvent.OnLoginClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.xl),
                isLoading = state.isLoggingIn,
                isEnabled = state.username.isNotBlank() && state.password.isNotBlank() && !state.isLoggingIn
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
