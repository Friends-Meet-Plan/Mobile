package friends.mobile.feature.friends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetFriendsResponseDto(
    @SerialName("friends") val friends: List<FriendDto>,
)
