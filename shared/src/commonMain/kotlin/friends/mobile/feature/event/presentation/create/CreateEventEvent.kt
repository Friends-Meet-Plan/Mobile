package friends.mobile.feature.event.presentation.create

/**
 * MVI events for the create event screen.
 *
 * Events are user actions that trigger state changes and side effects.
 *
 * Events:
 *   - ScreenOpened: triggered when the create event screen is first opened
 *   - OnTitleChanged: user enters/edits the event title
 *   - OnDescriptionChanged: user enters/edits the event description
 *   - OnLocationChanged: user enters/edits the event location
 *   - OnDateChanged: user changes the event date (not typical for creation, but supported)
 *   - FetchAvailableFriends: load friends who are free on the selected date
 *   - OnFriendSelected: user selects a friend to invite
 *   - OnFriendDeselected: user deselects a friend
 *   - OnCreateEventClicked: user submits the form to create an event
 *   - OnCancelClicked: user cancels and goes back
 */
sealed class CreateEventEvent {
    data object ScreenOpened : CreateEventEvent()

    data class OnTitleChanged(val title: String) : CreateEventEvent()
    data class OnDescriptionChanged(val description: String) : CreateEventEvent()
    data class OnLocationChanged(val location: String) : CreateEventEvent()
    data class OnDateChanged(val date: String) : CreateEventEvent()

    data object FetchAvailableFriends : CreateEventEvent()
    data class OnFriendSelected(val friendId: String) : CreateEventEvent()
    data class OnFriendDeselected(val friendId: String) : CreateEventEvent()

    data object OnCreateEventClicked : CreateEventEvent()
    data object OnCancelClicked : CreateEventEvent()
}
