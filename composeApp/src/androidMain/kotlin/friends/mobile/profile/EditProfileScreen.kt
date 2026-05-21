package friends.mobile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designkit.DesignTheme
import friends.mobile.designkit.FormTextField
import friends.mobile.designkit.PrimaryButton
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

    LaunchedEffect(viewModel) {
        viewModel.obtainEvent(EditProfileEvent.Init(initialUsername, initialBio, initialAvatarUrl))
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is EditProfileAction.NavigateBack -> onBack()
                is EditProfileAction.ShowMessage -> { /* Handle snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.obtainEvent(EditProfileEvent.OnBackClick) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is EditProfileViewState.Content -> {
                    EditContent(
                        state = currentState,
                        onEvent = viewModel::obtainEvent
                    )
                }
                is EditProfileViewState.Error -> {
                    ErrorState(message = currentState.message, onBack = onBack)
                }
            }
        }
    }
}

@Composable
private fun EditContent(
    state: EditProfileViewState.Content,
    onEvent: (EditProfileEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)
    ) {
        item {
            FormTextField(
                value = state.username,
                onValueChange = { onEvent(EditProfileEvent.OnUsernameChanged(it)) },
                placeholder = "Username",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )
        }

        item {
            FormTextField(
                value = state.bio,
                onValueChange = { onEvent(EditProfileEvent.OnBioChanged(it)) },
                placeholder = "Bio",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )
        }

        item {
            FormTextField(
                value = state.avatarUrl,
                onValueChange = { onEvent(EditProfileEvent.OnAvatarUrlChanged(it)) },
                placeholder = "Avatar URL",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(DesignTheme.Spacing.xxxl))
        }

        item {
            PrimaryButton(
                text = if (state.isSaving) "Saving..." else "Save",
                onClick = { onEvent(EditProfileEvent.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isSaving,
                isEnabled = !state.isSaving
            )
        }

        item {
            Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))
        }
    }
}

@Composable
private fun ErrorState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignTheme.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = DesignTheme.Colors.error,
            style = DesignTheme.Typography.body
        )
        Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))
        PrimaryButton(
            text = "Go Back",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
