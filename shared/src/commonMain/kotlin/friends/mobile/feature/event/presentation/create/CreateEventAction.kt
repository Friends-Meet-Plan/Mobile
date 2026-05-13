package friends.mobile.feature.event.presentation.create

/**
 * MVI actions (side effects) for the create event screen.
 *
 * Actions are emitted when the ViewModel needs to communicate with the UI layer
 * for things like navigation, showing alerts, etc.
 *
 * Actions:
 *   - ShowError: display an error message/toast to the user
 *   - EventCreatedSuccess: event was successfully created, navigate back
 *   - NavigateBack: go back to the previous screen
 *   - OpenFriendsSheet: open/present the friends selection sheet
 *   - CloseFriendsSheet: close the friends selection sheet
 */
sealed class CreateEventAction {
    data class ShowError(val message: String) : CreateEventAction()
    data object EventCreatedSuccess : CreateEventAction()
    data object NavigateBack : CreateEventAction()
    data object OpenFriendsSheet : CreateEventAction()
    data object CloseFriendsSheet : CreateEventAction()
}
