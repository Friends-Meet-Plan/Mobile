package friends.mobile.feature.friends.presentation

sealed class FriendProfileAction {
    data class ActionCompleted(val message: String) : FriendProfileAction()
}
