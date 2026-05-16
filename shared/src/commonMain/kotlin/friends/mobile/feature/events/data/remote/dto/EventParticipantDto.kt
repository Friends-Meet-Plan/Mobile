package friends.mobile.feature.events.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventParticipantDto(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val username: String = "",
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("role") val role: String,
    @SerialName("response_status") val status: String,
)
