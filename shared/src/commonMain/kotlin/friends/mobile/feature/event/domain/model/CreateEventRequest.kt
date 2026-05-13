package friends.mobile.feature.event.domain.model

/**
 * Domain model for creating an event.
 * This is used in the domain layer and passed to use cases.
 */
data class CreateEventRequest(
    val date: String,                       // "YYYY-MM-DD"
    val title: String,
    val description: String?,
    val location: String?,
    val participantIds: List<String>,       // UUIDs of invited friends
    val wishPlaceId: String? = null,
)
