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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.presentation.FriendProfileEvent
import friends.mobile.feature.friends.presentation.FriendProfileViewModel
import friends.mobile.feature.friends.presentation.FriendProfileViewState
import friends.mobile.feature.friends.presentation.FriendshipStatus
import kotlinx.coroutines.flow.collectLatest

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
fun FriendProfileScreenContent(userId: String) {
    val viewModel: FriendProfileViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            // Handle actions if needed
        }
    }

    LaunchedEffect(userId) {
        viewModel.obtainEvent(FriendProfileEvent.ScreenOpened(userId = userId))
    }

    val isLoading = state is FriendProfileViewState.Loading
    val errorMessage = (state as? FriendProfileViewState.Error)?.message
    val contentState = state as? FriendProfileViewState.Content

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        if (errorMessage != null && !isLoading) {
            ErrorBanner(
                message = errorMessage,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth(),
        ) {
            if (isLoading) {
                LoadingState()
            } else {
                contentState?.let { content ->
                    FriendProfileContent(
                        content = content,
                        onSendRequest = { userId ->
                            viewModel.obtainEvent(
                                FriendProfileEvent.OnSendRequest(userId = userId)
                            )
                        },
                        onAcceptRequest = { userId ->
                            viewModel.obtainEvent(
                                FriendProfileEvent.OnAcceptRequest(userId = userId)
                            )
                        },
                        onRejectRequest = { userId ->
                            viewModel.obtainEvent(
                                FriendProfileEvent.OnRejectRequest(userId = userId)
                            )
                        },
                        onRemoveFriend = { userId ->
                            viewModel.obtainEvent(
                                FriendProfileEvent.OnRemoveFriend(userId = userId)
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading profile...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserProfileCard(user = content.user)

        Spacer(modifier = Modifier.height(24.dp))

        if (content.actionError != null) {
            ErrorBanner(
                message = content.actionError!!,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        ActionButtons(
            user = content.user,
            status = content.status,
            isLoading = content.isActionPending,
            callbacks = FriendActionCallbacks(
                onSendRequest = onSendRequest,
                onAcceptRequest = onAcceptRequest,
                onRejectRequest = onRejectRequest,
                onRemoveFriend = onRemoveFriend,
            ),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun UserProfileCard(
    user: User,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user.username.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        user.bio?.let { bio ->
            if (bio.isNotEmpty()) {
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    user: User,
    status: FriendshipStatus,
    isLoading: Boolean,
    callbacks: FriendActionCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (status) {
            FriendshipStatus.NONE -> {
                Button(
                    onClick = { callbacks.onSendRequest(user.id) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Add Friend")
                    }
                }
            }

            FriendshipStatus.REQUESTING -> {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ) {
                    Text("Request Sent")
                }
            }

            FriendshipStatus.INCOMING -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = { callbacks.onAcceptRequest(user.id) },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                        ),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Accept")
                        }
                    }

                    OutlinedButton(
                        onClick = { callbacks.onRejectRequest(user.id) },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                    ) {
                        Text("Decline")
                    }
                }
            }

            FriendshipStatus.FRIENDS -> {
                OutlinedButton(
                    onClick = { callbacks.onRemoveFriend(user.id) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Remove Friend")
                    }
                }
            }
        }
    }
}
