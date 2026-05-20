package friends.mobile.feature.main.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventListItemDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("date") val date: String,
    @SerialName("creator_id") val creatorId: String,
    @SerialName("status") val status: String,
    @SerialName("participants") val participants: List<EventParticipantItemDto>,
)

@Serializable
data class EventParticipantItemDto(
    @SerialName("user_id") val userId: String,
)
