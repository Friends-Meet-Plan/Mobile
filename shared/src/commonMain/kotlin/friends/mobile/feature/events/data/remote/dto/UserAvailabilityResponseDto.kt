package friends.mobile.feature.events.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAvailabilityResponseDto(
    @SerialName("is_available") val isAvailable: Boolean,
)
