package friends.mobile.feature.events.presentation

sealed class CreateEventEvent {
    data class OnDateSelected(val date: String) : CreateEventEvent()
    data class OnToggleFriend(val friendId: String) : CreateEventEvent()
    data object OnContinueToDetails : CreateEventEvent()
    data class OnTitleChanged(val title: String) : CreateEventEvent()
    data class OnDescriptionChanged(val description: String) : CreateEventEvent()
    data class OnLocationChanged(val location: String) : CreateEventEvent()
    data class OnTimeChanged(val time: String) : CreateEventEvent()
    data object OnSubmit : CreateEventEvent()
    data object OnBack : CreateEventEvent()
}
