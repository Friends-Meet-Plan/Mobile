package friends.mobile.feature.wishplaces.presentation

import friends.mobile.feature.wishplaces.domain.model.WishPlace

sealed class WishPlacesViewState {
    data object Loading : WishPlacesViewState()
    data class Error(val message: String) : WishPlacesViewState()
    data class Content(
        val places: List<WishPlace> = emptyList(),
        val isRefreshing: Boolean = false,
        val isActionPending: Boolean = false
    ) : WishPlacesViewState()
}
