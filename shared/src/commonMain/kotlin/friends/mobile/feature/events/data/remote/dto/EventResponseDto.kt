package friends.mobile.feature.events.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventResponseDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String?,
    @SerialName("date") val date: String,
    @SerialName("time") val time: String? = "12:00",
    @SerialName("location") val location: String?,
    @SerialName("creator_id") val creatorId: String,
    @SerialName("status") val status: String,
    @SerialName("participants") val participants: List<EventParticipantDto>,
)
