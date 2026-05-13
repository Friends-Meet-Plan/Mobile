package friends.mobile.feature.event.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for event response from the backend.
 * Maps to EventResponse on the backend.
 */
@Serializable
data class EventResponseDto(
    val id: String,
    @SerialName("creator_id")
    val creatorId: String,
    val date: String,              // "YYYY-MM-DD"
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val status: String,            // "pending", "confirmed", "canceled", "completed"
    @SerialName("wish_place_id")
    val wishPlaceId: String? = null,
    @SerialName("memory_image_base64")
    val memoryImageBase64: String? = null,
    @SerialName("created_at")
    val createdAt: String,         // RFC3339
    val participants: List<ParticipantDto> = emptyList(),
)
