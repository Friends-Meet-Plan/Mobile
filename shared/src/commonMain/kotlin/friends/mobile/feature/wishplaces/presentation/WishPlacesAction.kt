package friends.mobile.feature.wishplaces.presentation

sealed class WishPlacesAction {
    data class ShowError(val message: String) : WishPlacesAction()
    data object PlaceCreated : WishPlacesAction()
}
