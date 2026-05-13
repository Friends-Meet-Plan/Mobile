package friends.mobile.feature.event.domain.model

data class Event(
    val id: String,
    val creatorId: String,
    val date: String,           // "YYYY-MM-DD"
    val title: String,
    val description: String?,
    val location: String?,
    val status: EventStatus,    // pending, confirmed, canceled, completed
    val wishPlaceId: String?,
    val memoryImageBase64: String?,
    val createdAt: String,      // RFC3339
    val participants: List<EventParticipant>,
)

enum class EventStatus(val displayName: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELED("Canceled"),
    COMPLETED("Completed");

    companion object {
        fun fromString(value: String): EventStatus {
            return when (value.lowercase()) {
                "pending" -> PENDING
                "confirmed" -> CONFIRMED
                "canceled" -> CANCELED
                "completed" -> COMPLETED
                else -> PENDING
            }
        }
    }
}

data class EventParticipant(
    val userId: String,
    val role: ParticipantRole,          // owner, participant
    val responseStatus: ParticipantResponseStatus,  // pending, accepted, declined
)

enum class ParticipantRole {
    OWNER,
    PARTICIPANT;

    companion object {
        fun fromString(value: String): ParticipantRole {
            return when (value.lowercase()) {
                "owner" -> OWNER
                "participant" -> PARTICIPANT
                else -> PARTICIPANT
            }
        }
    }
}

enum class ParticipantResponseStatus {
    PENDING,
    ACCEPTED,
    DECLINED;

    companion object {
        fun fromString(value: String): ParticipantResponseStatus {
            return when (value.lowercase()) {
                "pending" -> PENDING
                "accepted" -> ACCEPTED
                "declined" -> DECLINED
                else -> PENDING
            }
        }
    }
}
