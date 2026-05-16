package friends.mobile.feature.wishplaces.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishPlaceDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("link") val link: String? = null,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("visited_event_id") val visitedEventId: String? = null
)