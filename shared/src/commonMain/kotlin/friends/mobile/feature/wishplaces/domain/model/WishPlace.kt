package friends.mobile.feature.wishplaces.domain.model

data class WishPlace(
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val location: String?,
    val link: String?,
    val status: WishPlaceStatus,
    val createdAt: String,
    val visitedEventId: String?
)
