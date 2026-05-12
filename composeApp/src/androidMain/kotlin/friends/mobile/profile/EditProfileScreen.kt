package friends.mobile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.profile.presentation.edit.EditProfileAction
import friends.mobile.feature.profile.presentation.edit.EditProfileEvent
import friends.mobile.feature.profile.presentation.edit.EditProfileViewModel
import friends.mobile.feature.profile.presentation.edit.EditProfileViewState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialUsername: String,
    initialBio: String,
    initialAvatarUrl: String,
    onBack: () -> Unit,
) {
    val viewModel: EditProfileViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.obtainEvent(EditProfileEvent.Init(initialUsername, initialBio, initialAvatarUrl))
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is EditProfileAction.NavigateBack -> onBack()
                is EditProfileAction.ShowMessage -> { /* Handle message */ }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val currentState = state) {
                is EditProfileViewState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is EditProfileViewState.Error -> {
                    Text(text = currentState.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { onBack() }) {
                        Text("Go Back")
                    }
                }
                is EditProfileViewState.Content -> {
                    OutlinedTextField(
                        value = currentState.username,
                        onValueChange = { viewModel.obtainEvent(EditProfileEvent.OnUsernameChanged(it)) },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = currentState.bio,
                        onValueChange = { viewModel.obtainEvent(EditProfileEvent.OnBioChanged(it)) },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OutlinedTextField(
                        value = currentState.avatarUrl,
                        onValueChange = { viewModel.obtainEvent(EditProfileEvent.OnAvatarUrlChanged(it)) },
                        label = { Text("Avatar URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // ИСПРАВЛЕННЫЙ БЛОК ТУТ:
                    currentState.saveError?.let { errorText ->
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.obtainEvent(EditProfileEvent.OnSaveClick) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !currentState.isSaving
                    ) {
                        if (currentState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}
