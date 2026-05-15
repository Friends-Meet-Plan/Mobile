package friends.mobile.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileAction
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileEvent
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileViewModel
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileViewState
import friends.mobile.feature.friends.presentation.friendsProfile.FriendshipStatus
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
        containerColor = Color.Transparent // BottomSheet already has background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            when (val currentState = state) {
                is FriendProfileViewState.Loading -> LoadingState()
                is FriendProfileViewState.Error -> ErrorBanner(
                    message = currentState.message,
                    modifier = Modifier.fillMaxWidth()
                )
                is FriendProfileViewState.Content -> FriendProfileContent(
                    content = currentState,
                    onSendRequest = { id -> viewModel.obtainEvent(FriendProfileEvent.OnSendRequest(id)) },
                    onAcceptRequest = { id -> viewModel.obtainEvent(FriendProfileEvent.OnAcceptRequest(id)) },
                    onRejectRequest = { id -> viewModel.obtainEvent(FriendProfileEvent.OnRejectRequest(id)) },
                    onRemoveFriend = { id -> viewModel.obtainEvent(FriendProfileEvent.OnRemoveFriend(id)) }
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Loading profile...", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FriendProfileContent(
    content: FriendProfileViewState.Content,
    onSendRequest: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserProfileCard(user = content.user)

        Spacer(modifier = Modifier.height(24.dp))

        ActionButtons(
            user = content.user,
            status = content.status,
            isLoading = content.isActionPending,
            callbacks = FriendActionCallbacks(onSendRequest, onAcceptRequest, onRejectRequest, onRemoveFriend)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun UserProfileCard(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        Text(text = user.username, style = MaterialTheme.typography.headlineSmall)
        user.bio?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
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
        FriendshipStatus.NONE -> Button(
            onClick = { callbacks.onSendRequest(user.id) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            else Text("Add Friend")
        }
        FriendshipStatus.REQUESTING -> Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        ) { Text("Request Sent") }
        FriendshipStatus.INCOMING -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { callbacks.onAcceptRequest(user.id) }, modifier = Modifier.weight(1f), enabled = !isLoading) {
                Text("Accept")
            }
            OutlinedButton(onClick = { callbacks.onRejectRequest(user.id) }, modifier = Modifier.weight(1f), enabled = !isLoading) {
                Text("Decline")
            }
        }
        FriendshipStatus.FRIENDS -> OutlinedButton(
            onClick = { callbacks.onRemoveFriend(user.id) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) { Text("Remove Friend") }
    }
}
