package friends.mobile.feature.events.presentation.createevent

sealed class CreateEventEvent {
    data class OnTitleChanged(val title: String) : CreateEventEvent()
    data class OnDescriptionChanged(val description: String) : CreateEventEvent()
    data class OnLocationChanged(val location: String) : CreateEventEvent()
    data class OnToggleFriend(val friendId: String) : CreateEventEvent()
    data object OnCreateEvent : CreateEventEvent()
}
