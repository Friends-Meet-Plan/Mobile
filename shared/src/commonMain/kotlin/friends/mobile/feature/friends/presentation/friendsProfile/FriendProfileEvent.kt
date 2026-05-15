package friends.mobile.feature.friends.presentation.friendsProfile

/**
 * MVI events for the friend profile screen.
 *
 * Events:
 *   - ScreenOpened: triggered when viewing a friend's profile
 *   - OnSendRequest: send a friend request to this user
 *   - OnAcceptRequest: accept an incoming friend request from this user
 *   - OnRejectRequest: reject an incoming friend request from this user
 *   - OnRemoveFriend: remove this user from friends list
 */
sealed class FriendProfileEvent {
    data class ScreenOpened(val userId: String) : FriendProfileEvent()
    data class OnSendRequest(val userId: String) : FriendProfileEvent()
    data class OnAcceptRequest(val userId: String) : FriendProfileEvent()
    data class OnRejectRequest(val userId: String) : FriendProfileEvent()
    data class OnRemoveFriend(val userId: String) : FriendProfileEvent()
}
