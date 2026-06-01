package friends.mobile.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.R
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.FormErrorMessage
import friends.mobile.designsystem.components.FormSecureField
import friends.mobile.designsystem.components.FormTextField
import friends.mobile.designsystem.theme.DesignTheme
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

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var sideEffectError by remember { mutableStateOf<String?>(null) }

    val isLoggingIn = state is LoginViewState.Loading ||
            (state as? LoginViewState.Content)?.isLoggingIn == true
    val errorMessage = (state as? LoginViewState.Error)?.message ?: sideEffectError

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is LoginAction.NavigateToHome -> onLoginSuccess(action.session)
                is LoginAction.ShowMessage -> sideEffectError = action.message
            }
        }
    }

    LoginForm(
        username = username,
        password = password,
        errorMessage = errorMessage,
        isLoggingIn = isLoggingIn,
        onUsernameChanged = {
            username = it
            sideEffectError = null
            viewModel.obtainEvent(LoginEvent.OnUsernameChanged(it))
        },
        onPasswordChanged = {
            password = it
            sideEffectError = null
            viewModel.obtainEvent(LoginEvent.OnPasswordChanged(it))
        },
        onLoginClick = { viewModel.obtainEvent(LoginEvent.OnLoginClick) },
        onRegisterClick = { showRegister = true }
    )

    if (showRegister) {
        RegisterBottomSheet(
            onDismiss = { showRegister = false },
            onRegisterSuccess = { showRegister = false }
        )
    }
}

@Composable
private fun LoginForm(
    username: String,
    password: String,
    errorMessage: String?,
    isLoggingIn: Boolean,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            top = DesignTheme.Spacing.xxxl,
            bottom = DesignTheme.Spacing.xl
        )
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.onboarding_image),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DesignTheme.Spacing.sm, start = DesignTheme.Spacing.sm, end = DesignTheme.Spacing.sm)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
            ) {
                FormTextField(
                    value = username,
                    onValueChange = onUsernameChanged,
                    placeholder = stringResource(R.string.label_username),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false
                    )
                )

                FormSecureField(
                    value = password,
                    onValueChange = onPasswordChanged,
                    placeholder = stringResource(R.string.label_password),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                if (errorMessage != null) {
                    FormErrorMessage(message = errorMessage)
                }
            }
        }

        item {
            ButtonFactory.Primary(
                text = stringResource(R.string.auth_login),
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.xl),
                isLoading = isLoggingIn,
                isEnabled = username.isNotBlank() && password.isNotBlank() && !isLoggingIn
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.auth_no_account),
                    style = DesignTheme.Typography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.auth_sign_up),
                    modifier = Modifier.clickable { onRegisterClick() },
                    style = DesignTheme.Typography.captionSemibold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(DesignTheme.Spacing.xl))
        }
    }
}
