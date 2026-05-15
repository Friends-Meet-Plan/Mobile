package friends.mobile.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                is RegisterAction.ShowMessage -> { /* Handle snackbar if needed */ }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            when (val currentState = state) {
                is RegisterViewState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).padding(32.dp))
                }
                is RegisterViewState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = currentState.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.obtainEvent(RegisterEvent.OnUsernameChanged("")) }) {
                            Text("Try Again")
                        }
                    }
                }
                is RegisterViewState.Content -> {
                    RegisterForm(
                        state = currentState,
                        onEvent = viewModel::obtainEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterForm(
    state: RegisterViewState.Content,
    onEvent: (RegisterEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        Text(text = "Create Account", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.username,
            onValueChange = { onEvent(RegisterEvent.OnUsernameChanged(it)) },
            label = { Text("Username") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(RegisterEvent.OnPasswordChanged(it)) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = { onEvent(RegisterEvent.OnRegisterClick) },
            enabled = !state.isRegistering && state.username.isNotBlank() && state.password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
        ) {
            if (state.isRegistering) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register")
            }
        }
    }
}
