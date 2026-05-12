package friends.mobile.feature.friends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendIdBody(
    @SerialName("friend_id")
    val friendId: String,
)
