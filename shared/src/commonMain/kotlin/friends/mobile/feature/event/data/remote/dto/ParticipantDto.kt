package friends.mobile.feature.event.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for event participant information.
 * Maps to ParticipantResponse on the backend.
 */
@Serializable
data class ParticipantDto(
    @SerialName("user_id")
    val userId: String,
    val role: String,              // "owner", "participant"
    @SerialName("response_status")
    val responseStatus: String,    // "pending", "accepted", "declined"
)
