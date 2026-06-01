package friends.mobile.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.R
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.FormErrorMessage
import friends.mobile.designsystem.components.FormSecureField
import friends.mobile.designsystem.components.FormTextField
import friends.mobile.designsystem.theme.DesignTheme
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

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val isRegistering = state is RegisterViewState.Loading ||
        (state as? RegisterViewState.Content)?.isRegistering == true
    val errorMessage = (state as? RegisterViewState.Error)?.message

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                RegisterAction.RegisterSucceeded -> onRegisterSuccess()
                is RegisterAction.ShowMessage -> {}
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xxl),
            contentPadding = PaddingValues(vertical = DesignTheme.Spacing.lg, horizontal = 0.dp)
        ) {
            item {
                RegisterHeader(onDismiss = onDismiss)
            }
            item {
                RegisterForm(
                    username = username,
                    password = password,
                    errorMessage = errorMessage,
                    onUsernameChange = {
                        username = it
                        viewModel.obtainEvent(RegisterEvent.OnUsernameChanged(it))
                    },
                    onPasswordChange = {
                        password = it
                        viewModel.obtainEvent(RegisterEvent.OnPasswordChanged(it))
                    },
                )
            }
            item {
                RegisterButton(
                    isRegistering = isRegistering,
                    isEnabled = username.isNotBlank() && password.isNotBlank() && !isRegistering,
                    onClick = { viewModel.obtainEvent(RegisterEvent.OnRegisterClick) },
                )
            }
            item {
                LoginPrompt(onLoginClick = onDismiss)
            }
            item {
                Spacer(modifier = Modifier.height(DesignTheme.Spacing.xl))
            }
        }
    }
}

@Composable
private fun RegisterHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignTheme.Spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.auth_create_account),
            style = DesignTheme.Typography.heading,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun RegisterForm(
    username: String,
    password: String,
    errorMessage: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignTheme.Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
    ) {
        FormTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = stringResource(R.string.label_username),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false
            )
        )
        FormSecureField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.label_password),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (errorMessage != null) {
            FormErrorMessage(message = errorMessage)
        }
    }
}

@Composable
private fun RegisterButton(
    isRegistering: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    ButtonFactory.Primary(
        text = stringResource(R.string.auth_create_account),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignTheme.Spacing.xl),
        isLoading = isRegistering,
        isEnabled = isEnabled,
    )
}

@Composable
private fun LoginPrompt(onLoginClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignTheme.Spacing.xl)
    ) {
        Text(
            text = stringResource(R.string.auth_already_have_account),
            style = DesignTheme.Typography.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(DesignTheme.Spacing.xs))
        Text(
            text = stringResource(R.string.auth_login),
            modifier = Modifier.clickable { onLoginClick() },
            style = DesignTheme.Typography.captionSemibold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
