package friends.mobile.feature.wishplaces.presentation

sealed class WishPlacesEvent {
    data class LoadPlaces(
        val userId: String
    ) : WishPlacesEvent()

    data class CreatePlace(
        val userId: String,
        val title: String,
        val description: String?,
        val location: String?,
        val link: String?,
    ) : WishPlacesEvent()

    data class ArchivePlace(
        val userId: String,
        val id: String,
    ) : WishPlacesEvent()
}
