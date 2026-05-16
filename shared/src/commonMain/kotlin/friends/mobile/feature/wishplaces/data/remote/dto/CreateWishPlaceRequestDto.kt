package friends.mobile.feature.wishplaces.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWishPlaceRequestDto(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String?,
    @SerialName("location") val location: String?,
    @SerialName("link") val link: String?
)
