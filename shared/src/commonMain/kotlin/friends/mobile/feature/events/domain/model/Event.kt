package friends.mobile.feature.events.domain.model

data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val date: String,
    val time: String?,
    val location: String?,
    val creatorId: String,
    val participants: List<EventParticipant>,
)
