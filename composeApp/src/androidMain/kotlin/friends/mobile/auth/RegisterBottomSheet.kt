package friends.mobile.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.MaterialTheme
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.ButtonFactory
import friends.mobile.designkit.components.FormErrorMessage
import friends.mobile.designkit.components.FormSecureField
import friends.mobile.designkit.components.FormTextField
import friends.mobile.designkit.components.LoadingView
import friends.mobile.feature.auth.presentation.register.RegisterAction
import friends.mobile.feature.auth.presentation.register.RegisterEvent
import friends.mobile.feature.auth.presentation.register.RegisterViewModel
import friends.mobile.feature.auth.presentation.register.RegisterViewState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBottomSheet(
    onDismiss: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                RegisterAction.RegisterSucceeded -> onRegisterSuccess()
                is RegisterAction.ShowMessage -> { /* handled inline via ViewState.Error */ }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        when (val currentState = state) {
            is RegisterViewState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = DesignTheme.Spacing.xxxl),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingView(
                        modifier = Modifier.fillMaxWidth(),
                        message = "Creating account..."
                    )
                }
            }
            is RegisterViewState.Error -> {
                RegisterForm(
                    state = RegisterViewState.Content(),
                    errorMessage = currentState.message,
                    onEvent = viewModel::obtainEvent,
                    onDismiss = onDismiss
                )
            }
            is RegisterViewState.Content -> {
                RegisterForm(
                    state = currentState,
                    errorMessage = null,
                    onEvent = viewModel::obtainEvent,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun RegisterForm(
    state: RegisterViewState.Content,
    errorMessage: String?,
    onEvent: (RegisterEvent) -> Unit,
    onDismiss: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xxl),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            vertical = DesignTheme.Spacing.lg,
            horizontal = 0.dp
        )
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.xl),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Create Account",
                    style = DesignTheme.Typography.heading,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                }
            }
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
                    onValueChange = { onEvent(RegisterEvent.OnUsernameChanged(it)) },
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
                    onValueChange = { onEvent(RegisterEvent.OnPasswordChanged(it)) },
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
                text = "Create Account",
                onClick = { onEvent(RegisterEvent.OnRegisterClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.xl),
                isLoading = state.isRegistering,
                isEnabled = state.username.isNotBlank() && state.password.isNotBlank() && !state.isRegistering
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.xl)
            ) {
                Text(
                    text = "Already have an account?",
                    style = DesignTheme.Typography.caption,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(DesignTheme.Spacing.xs))
                Text(
                    text = "Login",
                    modifier = Modifier.clickable { onDismiss() },
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
