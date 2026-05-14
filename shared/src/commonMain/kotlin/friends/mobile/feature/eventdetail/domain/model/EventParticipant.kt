package friends.mobile.feature.eventdetail.domain.model

data class EventParticipant(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val bio: String?,
    val role: String,  // "owner" or "participant"
    val responseStatus: String,  // "accepted", "pending", "declined"
)
