package friends.mobile.feature.friends.presentation

/**
 * MVI events for the friends screen.
 *
 * Events:
 *   - ScreenOpened: triggered when the friends screen is first opened
 *   - OnFriendClick: user tapped on a friend in the list (navigate to friend profile)
 */
sealed class FriendsEvent {
    data object ScreenOpened : FriendsEvent()
}
