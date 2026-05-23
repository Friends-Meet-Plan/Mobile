package friends.mobile.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designkit.DesignTheme
import friends.mobile.designkit.PrimaryButton
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileAction
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileEvent
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileViewModel
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileViewState
import friends.mobile.feature.friends.presentation.friendsProfile.FriendshipStatus
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import friends.mobile.wishplaces.WishPlaceDetailBottomSheet
import friends.mobile.wishplaces.WishPlaceItem
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
            containerColor = MaterialTheme.colorScheme.surface,
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
                is FriendProfileAction.ShowError -> {
                    snackbarHostState.showSnackbar(action.message)
                }
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
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when (val currentState = state) {
                is FriendProfileViewState.Loading -> LoadingState()
                is FriendProfileViewState.Error -> ErrorBanner(
                    message = currentState.message,
                    modifier = Modifier.fillMaxWidth()
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
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DesignTheme.Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))
        Text(text = "Loading profile...", style = DesignTheme.Typography.body)
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
            .padding(DesignTheme.Spacing.lg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserProfileCard(user = content.user)
        Spacer(modifier = Modifier.height(DesignTheme.Spacing.xxl))
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

            Spacer(modifier = Modifier.height(DesignTheme.Spacing.xxxl))

            Text(
                text = "${content.user.username}'s Wish Places",
                style = DesignTheme.Typography.heading,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = DesignTheme.Spacing.lg)
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
private fun UserProfileCard(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .background(DesignTheme.Colors.textField)
            .padding(DesignTheme.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(DesignTheme.Colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.firstOrNull()?.uppercase() ?: "?",
                style = DesignTheme.Typography.heading.copy(color = DesignTheme.Colors.primary)
            )
        }
        Text(text = user.username, style = DesignTheme.Typography.heading)
        user.bio?.let { Text(text = it, style = DesignTheme.Typography.body) }
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
        FriendshipStatus.NONE -> PrimaryButton(
            text = "Add Friend",
            onClick = { callbacks.onSendRequest(user.id) },
            isLoading = isLoading,
            isEnabled = !isLoading
        )
        FriendshipStatus.REQUESTING -> PrimaryButton(
            text = "Request Sent",
            onClick = {},
            isEnabled = false
        )
        FriendshipStatus.INCOMING -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
        ) {
            PrimaryButton(
                text = "Accept",
                onClick = { callbacks.onAcceptRequest(user.id) },
                modifier = Modifier.weight(1f),
                isLoading = isLoading,
                isEnabled = !isLoading
            )
            DeclineButton(
                onClick = { callbacks.onRejectRequest(user.id) },
                isLoading = isLoading,
                isEnabled = !isLoading,
                modifier = Modifier.weight(1f)
            )
        }
        FriendshipStatus.FRIENDS -> DeclineButton(
            text = "Remove Friend",
            onClick = { callbacks.onRemoveFriend(user.id) },
            isLoading = isLoading,
            isEnabled = !isLoading
        )
    }
}

@Composable
private fun DeclineButton(
    text: String = "Decline",
    onClick: () -> Unit,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.capsule))
            .background(
                if (isEnabled) DesignTheme.Colors.primary.copy(alpha = 0.1f)
                else DesignTheme.Colors.primary.copy(alpha = 0.05f)
            ),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            enabled = isEnabled && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = DesignTheme.Colors.primary,
                        strokeWidth = 2.dp
                    )
                    Text(text, style = DesignTheme.Typography.button.copy(color = DesignTheme.Colors.primary))
                }
            } else {
                Text(text, style = DesignTheme.Typography.button.copy(color = DesignTheme.Colors.primary))
            }
        }
    }
}
