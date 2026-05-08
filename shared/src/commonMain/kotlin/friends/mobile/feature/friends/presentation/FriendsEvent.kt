package friends.mobile.feature.friends.presentation

/**
 * MVI events for the friends screen.
 *
 * Events:
 *   - ScreenOpened: triggered when the friends screen is first opened
 *   - OnSendRequest: send a friend request to a user
 *   - OnAcceptRequest: accept an incoming friend request
 *   - OnRejectRequest: reject an incoming friend request
 *   - OnCancelRequest: cancel an outgoing friend request
 *   - OnSearchUsers: search for users by name/username
 */
sealed class FriendsEvent {
    data object ScreenOpened : FriendsEvent()
    data class OnSendRequest(val friendId: String) : FriendsEvent()
    data class OnAcceptRequest(val requestId: String) : FriendsEvent()
    data class OnRejectRequest(val requestId: String) : FriendsEvent()
    data class OnCancelRequest(val requestId: String) : FriendsEvent()
    data class OnSearchUsers(val query: String) : FriendsEvent()
}
