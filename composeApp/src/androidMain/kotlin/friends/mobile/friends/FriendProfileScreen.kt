package friends.mobile.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.Dimension
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.designsystem.components.UserView
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileAction
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileEvent
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileViewModel
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileViewState
import friends.mobile.feature.friends.presentation.friendsProfile.FriendshipStatus
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import friends.mobile.wishplaces.WishPlaceDetailBottomSheet
import friends.mobile.wishplaces.WishPlacesMode
import friends.mobile.wishplaces.WishPlacesSection
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

private data class FriendActionCallbacks(
    val onSendRequest: (String) -> Unit,
    val onAcceptRequest: (String) -> Unit,
    val onRejectRequest: (String) -> Unit,
    val onRemoveFriend: (String) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendProfileBottomSheet(
    userId: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSheetDismissed: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                onSheetDismissed()
                onDismiss()
            },
            sheetState = sheetState,
        ) {
            FriendProfileScreenContent(userId = userId)
        }
    }
}

@Composable
fun FriendProfileScreenContent(
    userId: String,
    viewModel: FriendProfileViewModel = koinViewModel()
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedPlace by remember { mutableStateOf<WishPlace?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is FriendProfileAction.ShowError -> snackbarHostState.showSnackbar(action.message)
            }
        }
    }

    LaunchedEffect(userId) {
        viewModel.obtainEvent(FriendProfileEvent.ScreenOpened(userId = userId))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            when (val currentState = state) {
                is FriendProfileViewState.Loading -> LoadingView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
                is FriendProfileViewState.Error -> ErrorBanner(
                    message = currentState.message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignTheme.Spacing.lg)
                )
                is FriendProfileViewState.Content -> {
                    FriendProfileContent(
                        content = currentState,
                        onSendRequest = { id -> viewModel.obtainEvent(FriendProfileEvent.OnSendRequest(id)) },
                        onAcceptRequest = { id -> viewModel.obtainEvent(FriendProfileEvent.OnAcceptRequest(id)) },
                        onRejectRequest = { id -> viewModel.obtainEvent(FriendProfileEvent.OnRejectRequest(id)) },
                        onRemoveFriend = { id -> viewModel.obtainEvent(FriendProfileEvent.OnRemoveFriend(id)) },
                        onPlaceClick = { selectedPlace = it }
                    )
                }
            }
        }
    }

    selectedPlace?.let { place ->
        WishPlaceDetailBottomSheet(
            place = place,
            onDismiss = { selectedPlace = null }
        )
    }
}

@Composable
private fun FriendProfileContent(
    content: FriendProfileViewState.Content,
    onSendRequest: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onPlaceClick: (WishPlace) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignTheme.Spacing.xl, vertical = DesignTheme.Spacing.xl)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserView(
            username = content.user.username,
            bio = content.user.bio,
            dimension = Dimension.Vertical,
            modifier = Modifier.fillMaxWidth()
        )

        if (content.actionError != null) {
            ErrorBanner(
                message = content.actionError!!,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ActionButtons(
            user = content.user,
            status = content.status,
            isLoading = content.isActionPending,
            callbacks = FriendActionCallbacks(
                onSendRequest = onSendRequest,
                onAcceptRequest = onAcceptRequest,
                onRejectRequest = onRejectRequest,
                onRemoveFriend = onRemoveFriend
            )
        )

        if (content.status == FriendshipStatus.FRIENDS) {
            Text(
                text = "Wish Places",
                style = DesignTheme.Typography.heading,
                modifier = Modifier.align(Alignment.Start)
            )
            WishPlacesSection(
                userId = content.user.id,
                mode = WishPlacesMode.READ_ONLY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(DesignTheme.Spacing.xxxl))
    }
}

@Composable
private fun ActionButtons(
    user: User,
    status: FriendshipStatus,
    isLoading: Boolean,
    callbacks: FriendActionCallbacks
) {
    when (status) {
        FriendshipStatus.NONE -> ButtonFactory.Primary(
            text = "Add Friend",
            onClick = { callbacks.onSendRequest(user.id) },
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
            isEnabled = !isLoading
        )

        FriendshipStatus.REQUESTING -> ButtonFactory.Disabled(
            text = "Request Sent",
            modifier = Modifier.fillMaxWidth(),
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        FriendshipStatus.INCOMING -> Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
        ) {
            ButtonFactory.Primary(
                text = "Accept",
                onClick = { callbacks.onAcceptRequest(user.id) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                isEnabled = !isLoading
            )
            ButtonFactory.Secondary(
                text = "Decline",
                onClick = { callbacks.onRejectRequest(user.id) },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = !isLoading
            )
        }

        FriendshipStatus.FRIENDS -> ButtonFactory.Destructive(
            text = "Remove Friend",
            onClick = { callbacks.onRemoveFriend(user.id) },
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading
        )
    }
}
