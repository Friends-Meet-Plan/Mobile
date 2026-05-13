package friends.mobile.feature.events.domain.model

data class EventParticipant(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val status: ParticipationStatus,
)
