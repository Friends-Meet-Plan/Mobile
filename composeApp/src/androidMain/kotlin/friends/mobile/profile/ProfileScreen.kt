package friends.mobile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import friends.mobile.feature.profile.domain.model.Profile
import friends.mobile.feature.profile.presentation.profile.ProfileAction
import friends.mobile.feature.profile.presentation.profile.ProfileEvent
import friends.mobile.feature.profile.presentation.profile.ProfileViewModel
import friends.mobile.feature.profile.presentation.profile.ProfileViewState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onEditClick: (Profile) -> Unit,
) {
    val viewModel: ProfileViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    // В ProfileScreen.kt внутри LaunchedEffect(viewModel)
    LaunchedEffect(Unit) {
        viewModel.obtainEvent(ProfileEvent.OnLoadProfile)
    }

    LaunchedEffect(viewModel) {
        viewModel.obtainEvent(ProfileEvent.OnLoadProfile)
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is ProfileAction.LogoutRequested -> onLogout()
                is ProfileAction.ShowMessage -> { /* Handle message */ }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Profile") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is ProfileViewState.Loading -> {
                    CircularProgressIndicator()
                }
                is ProfileViewState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = currentState.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.obtainEvent(ProfileEvent.OnLoadProfile) }) {
                            Text("Retry")
                        }
                    }
                }
                is ProfileViewState.Content -> {
                    val profile = currentState.profile
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = profile.username,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        profile.bio?.let {
                            Text(text = it, style = MaterialTheme.typography.bodyLarge)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onEditClick(profile) },
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Text("Edit Profile")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.obtainEvent(ProfileEvent.OnLogoutClick) },
                            enabled = !currentState.isLoggingOut,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            if (currentState.isLoggingOut) {
                                CircularProgressIndicator(
                                    modifier = Modifier.height(20.dp),
                                    color = MaterialTheme.colorScheme.onError
                                )
                            } else {
                                Text("Logout")
                            }
                        }
                    }
                }
            }
        }
    }
}
