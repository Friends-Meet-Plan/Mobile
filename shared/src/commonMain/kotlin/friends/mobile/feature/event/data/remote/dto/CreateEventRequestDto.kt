package friends.mobile.feature.event.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for creating an event.
 * Maps to CreateEventBody on the backend.
 *
 * Note: Fields use @SerialName to map from Kotlin camelCase to backend snake_case.
 */
@Serializable
data class CreateEventRequestDto(
    val date: String,                               // "YYYY-MM-DD"
    val title: String,
    val description: String? = null,
    val location: String? = null,
    @SerialName("participant_ids")
    val participantIds: List<String>,               // UUIDs
    @SerialName("wish_place_id")
    val wishPlaceId: String? = null,
)
