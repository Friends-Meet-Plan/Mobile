package friends.mobile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.feature.profile.domain.model.Profile
import friends.mobile.feature.profile.presentation.profile.ProfileAction
import friends.mobile.feature.profile.presentation.profile.ProfileEvent
import friends.mobile.feature.profile.presentation.profile.ProfileViewModel
import friends.mobile.feature.profile.presentation.profile.ProfileViewState
import friends.mobile.wishplaces.WishPlacesMode
import friends.mobile.wishplaces.WishPlacesSection
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

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(ProfileEvent.OnRefreshProfile)
    }

    LaunchedEffect(viewModel) {

        viewModel.viewActions.collectLatest { action ->

            when (action) {

                is ProfileAction.NavigateToLogin -> {
                    onLogout()
                }

                is ProfileAction.NavigateToEdit -> {
                    onEditClick(action.profile)
                }

                is ProfileAction.ShowMessage -> {
                    snackbarHostState.showSnackbar(action.message)
                }
            }
        }
    }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },

        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (val current = state) {

                is ProfileViewState.Loading -> {

                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ProfileViewState.Error -> {

                    ErrorContent(
                        message = current.message,
                        onRetry = {
                            viewModel.obtainEvent(
                                ProfileEvent.OnRefreshProfile
                            )
                        }
                    )
                }

                is ProfileViewState.Content -> {

                    ProfileContent(
                        profile = current.profile,
                        isLoggingOut = current.isLoggingOut,

                        onEditClick = {
                            viewModel.obtainEvent(
                                ProfileEvent.OnEditClick
                            )
                        },

                        onLogoutClick = {
                            viewModel.obtainEvent(
                                ProfileEvent.OnLogoutClick
                            )
                        }
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
    onLogoutClick: () -> Unit,
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        item {

            ProfileHeader(
                profile = profile,
                isLoggingOut = isLoggingOut,
                onEditClick = onEditClick,
                onLogoutClick = onLogoutClick
            )
        }

        item {

            Text(
                text = "My Wish Places",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {

            WishPlacesSection(
                userId = profile.id,
                mode = WishPlacesMode.EDITABLE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: Profile,
    isLoggingOut: Boolean,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = profile.username,
            style = MaterialTheme.typography.headlineMedium
        )

        profile.bio?.let { bio ->

            Text(
                text = bio,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = onEditClick,
                modifier = Modifier.weight(1f),
                enabled = !isLoggingOut
            ) {

                Text("Edit Profile")
            }

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.weight(1f),
                enabled = !isLoggingOut,

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {

                if (isLoggingOut) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )

                } else {

                    Text("Logout")
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = message,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onRetry
        ) {

            Text("Retry")
        }
    }
}
