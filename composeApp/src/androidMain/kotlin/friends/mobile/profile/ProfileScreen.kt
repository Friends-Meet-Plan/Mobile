package friends.mobile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import friends.mobile.calendar.BusyDaysView
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.ButtonFactory
import friends.mobile.designkit.components.ErrorBanner
import friends.mobile.designkit.components.LoadingView
import friends.mobile.designkit.components.UserView
import friends.mobile.designkit.components.Dimension
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(ProfileEvent.OnRefreshProfile)
    }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is ProfileAction.NavigateToLogin -> onLogout()
                is ProfileAction.NavigateToEdit -> onEditClick(action.profile)
                is ProfileAction.ShowMessage -> snackbarHostState.showSnackbar(action.message)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = DesignTheme.Typography.captionSemibold
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val current = state) {
                is ProfileViewState.Loading -> {
                    LoadingView()
                }
                is ProfileViewState.Error -> {
                    ProfileErrorContent(
                        message = current.message,
                        onRetry = { viewModel.obtainEvent(ProfileEvent.OnRefreshProfile) }
                    )
                }
                is ProfileViewState.Content -> {
                    ProfileContent(
                        profile = current.profile,
                        isLoggingOut = current.isLoggingOut,
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
    onLogoutClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xxl)
    ) {
        item {
            UserView(
                username = profile.username,
                bio = profile.bio,
                dimension = Dimension.Vertical,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            BusyDaysView(
                userId = profile.id,
                modifier = Modifier.fillMaxWidth()
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
            ) {
                ButtonFactory.primary(
                    text = "Edit Profile",
                    onClick = onEditClick,
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = !isLoggingOut
                )

                ButtonFactory.destructive(
                    text = "Log Out",
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = isLoggingOut
                )
            }
        }
    }
}

@Composable
private fun ProfileErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignTheme.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ErrorBanner(message = message)

        ButtonFactory.primary(
            text = "Retry",
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = DesignTheme.Spacing.lg)
        )
    }
}
