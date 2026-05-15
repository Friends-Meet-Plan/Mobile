package friends.mobile.feature.friends.presentation.friendsProfile

sealed class FriendProfileAction {
    data class ActionCompleted(val message: String) : FriendProfileAction()
    data class ShowError(val message: String) : FriendProfileAction()
}
