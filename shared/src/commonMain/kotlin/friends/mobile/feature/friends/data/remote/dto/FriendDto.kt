package friends.mobile.feature.friends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("bio") val bio: String? = null,
)
