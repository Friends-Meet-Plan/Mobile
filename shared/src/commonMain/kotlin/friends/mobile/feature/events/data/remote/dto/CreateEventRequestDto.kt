package friends.mobile.feature.events.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequestDto(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String?,
    @SerialName("date") val date: String,
    @SerialName("time") val time: String?,
    @SerialName("location") val location: String?,
    @SerialName("invited_friend_ids") val invitedFriendIds: List<String>,
)
