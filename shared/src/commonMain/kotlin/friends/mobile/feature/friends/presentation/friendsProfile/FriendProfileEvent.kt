package friends.mobile.feature.friends.presentation.friendsProfile

sealed class FriendProfileEvent {
    data class ScreenOpened(val userId: String) : FriendProfileEvent()
    data class OnSendRequest(val userId: String) : FriendProfileEvent()
    data class OnAcceptRequest(val userId: String) : FriendProfileEvent()
    data class OnRejectRequest(val userId: String) : FriendProfileEvent()
    data class OnRemoveFriend(val userId: String) : FriendProfileEvent()
}
