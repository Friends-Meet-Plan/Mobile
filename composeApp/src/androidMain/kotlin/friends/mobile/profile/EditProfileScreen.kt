package friends.mobile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.components.FormTextField
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.profile.presentation.edit.EditProfileAction
import friends.mobile.feature.profile.presentation.edit.EditProfileEvent
import friends.mobile.feature.profile.presentation.edit.EditProfileViewModel
import friends.mobile.feature.profile.presentation.edit.EditProfileViewState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialUsername: String,
    initialBio: String,
    initialAvatarUrl: String,
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel(),
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.obtainEvent(EditProfileEvent.Init(initialUsername, initialBio, initialAvatarUrl))
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is EditProfileAction.NavigateBack -> onBack()
                is EditProfileAction.ShowMessage -> snackbarHostState.showSnackbar(action.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.obtainEvent(EditProfileEvent.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val currentState = state) {
                is EditProfileViewState.Loading -> {
                    LoadingView(modifier = Modifier.fillMaxSize())
                }

                is EditProfileViewState.Content -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DesignTheme.Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)
                    ) {
                        FormTextField(
                            value = currentState.username,
                            onValueChange = { viewModel.obtainEvent(EditProfileEvent.OnUsernameChanged(it)) },
                            placeholder = "Username",
                            modifier = Modifier.fillMaxWidth()
                        )
                        FormTextField(
                            value = currentState.bio,
                            onValueChange = { viewModel.obtainEvent(EditProfileEvent.OnBioChanged(it)) },
                            placeholder = "Bio",
                            modifier = Modifier.fillMaxWidth()
                        )
                        FormTextField(
                            value = currentState.avatarUrl,
                            onValueChange = { viewModel.obtainEvent(EditProfileEvent.OnAvatarUrlChanged(it)) },
                            placeholder = "Avatar URL",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        ButtonFactory.Primary(
                            text = "Save",
                            onClick = { viewModel.obtainEvent(EditProfileEvent.OnSaveClick) },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = currentState.isSaving,
                            isEnabled = !currentState.isSaving
                        )
                    }
                }

                is EditProfileViewState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DesignTheme.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ErrorBanner(message = currentState.message)
                        ButtonFactory.Primary(
                            text = "Go Back",
                            onClick = onBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = DesignTheme.Spacing.lg)
                        )
                    }
                }
            }
        }
    }
}
