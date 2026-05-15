package friends.mobile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import friends.mobile.feature.profile.domain.model.Profile
import friends.mobile.feature.profile.presentation.profile.ProfileAction
import friends.mobile.feature.profile.presentation.profile.ProfileEvent
import friends.mobile.feature.profile.presentation.profile.ProfileViewModel
import friends.mobile.feature.profile.presentation.profile.ProfileViewState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onEditClick: (Profile) -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    // Refresh data when screen becomes visible again (e.g. returning from Edit)
    LaunchedEffect(Unit) {
        viewModel.obtainEvent(ProfileEvent.OnRefreshProfile)
    }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is ProfileAction.NavigateToLogin -> onLogout()
                is ProfileAction.NavigateToEdit -> onEditClick(action.profile)
                is ProfileAction.ShowMessage -> {
                    // Handle message showing
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Profile", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is ProfileViewState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileViewState.Error -> {
                    ErrorContent(
                        message = currentState.message,
                        onRetry = { viewModel.obtainEvent(ProfileEvent.OnLoadProfile) }
                    )
                }
                is ProfileViewState.Content -> {
                    ProfileContent(
                        profile = currentState.profile,
                        isLoggingOut = currentState.isLoggingOut,
                        onEditClick = { viewModel.obtainEvent(ProfileEvent.OnEditClick) },
                        onLogoutClick = { viewModel.obtainEvent(ProfileEvent.OnLogoutClick) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: Profile,
    isLoggingOut: Boolean,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = profile.username, style = MaterialTheme.typography.headlineMedium)
        profile.bio?.let { Text(text = it, style = MaterialTheme.typography.bodyLarge) }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onEditClick, 
            modifier = Modifier.fillMaxWidth(0.7f),
            enabled = !isLoggingOut
        ) {
            Text("Edit Profile")
        }

        Button(
            onClick = onLogoutClick,
            enabled = !isLoggingOut,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            if (isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onError,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
