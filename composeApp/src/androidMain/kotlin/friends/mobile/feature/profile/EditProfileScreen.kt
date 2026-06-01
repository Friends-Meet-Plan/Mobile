package friends.mobile.feature.profile

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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.R
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
            EditProfileTopBar(onBackClick = { viewModel.obtainEvent(EditProfileEvent.OnBackClick) })
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
                    EditProfileContent(
                        state = currentState,
                        onUsernameChange = { viewModel.obtainEvent(EditProfileEvent.OnUsernameChanged(it)) },
                        onBioChange = { viewModel.obtainEvent(EditProfileEvent.OnBioChanged(it)) },
                        onAvatarUrlChange = { viewModel.obtainEvent(EditProfileEvent.OnAvatarUrlChanged(it)) },
                        onSaveClick = { viewModel.obtainEvent(EditProfileEvent.OnSaveClick) },
                    )
                }
                is EditProfileViewState.Error -> {
                    EditProfileErrorContent(
                        message = currentState.message,
                        onBack = onBack,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.profile_edit)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun EditProfileContent(
    state: EditProfileViewState.Content,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onAvatarUrlChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)
    ) {
        FormTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            placeholder = stringResource(R.string.label_username),
            modifier = Modifier.fillMaxWidth()
        )
        FormTextField(
            value = state.bio,
            onValueChange = onBioChange,
            placeholder = stringResource(R.string.label_bio),
            modifier = Modifier.fillMaxWidth()
        )
        FormTextField(
            value = state.avatarUrl,
            onValueChange = onAvatarUrlChange,
            placeholder = stringResource(R.string.label_avatar_url),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        ButtonFactory.Primary(
            text = stringResource(R.string.save),
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isSaving,
            isEnabled = !state.isSaving
        )
    }
}

@Composable
private fun EditProfileErrorContent(
    message: String,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignTheme.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ErrorBanner(message = message)
        ButtonFactory.Primary(
            text = stringResource(R.string.go_back),
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = DesignTheme.Spacing.lg)
        )
    }
}
