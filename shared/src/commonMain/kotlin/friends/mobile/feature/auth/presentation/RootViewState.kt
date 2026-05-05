package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.model.AuthSession

sealed class RootViewState {
    data object Loading : RootViewState()
    data class Error(val message: String) : RootViewState()
    data class Content(val session: AuthSession?) : RootViewState()
}

