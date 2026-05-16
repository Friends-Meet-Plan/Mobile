package friends.mobile.feature.events.data.remote.dto

import friends.mobile.feature.auth.data.remote.dto.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckAvailabilityResponseDto(
    @SerialName("available_friends") val availableFriends: List<UserDto>,
)
