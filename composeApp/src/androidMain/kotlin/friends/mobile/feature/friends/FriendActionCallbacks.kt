package friends.mobile.feature.friends

internal data class FriendActionCallbacks(
    val onSendRequest: (String) -> Unit,
    val onAcceptRequest: (String) -> Unit,
    val onRejectRequest: (String) -> Unit,
    val onRemoveFriend: (String) -> Unit,
)
