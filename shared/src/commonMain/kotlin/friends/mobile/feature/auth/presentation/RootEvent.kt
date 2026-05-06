package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.model.AuthSession

sealed class RootEvent {
    data class OnSessionStarted(val session: AuthSession) : RootEvent()
    data object OnLogoutClick : RootEvent()
}
