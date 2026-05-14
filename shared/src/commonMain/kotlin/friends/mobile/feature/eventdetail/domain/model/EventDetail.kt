package friends.mobile.feature.eventdetail.domain.model

data class EventDetail(
    val id: String,
    val title: String,
    val description: String?,
    val date: String,
    val time: String?,
    val location: String?,
    val creatorId: String,
    val status: String,
    val participants: List<EventParticipant>,
)
