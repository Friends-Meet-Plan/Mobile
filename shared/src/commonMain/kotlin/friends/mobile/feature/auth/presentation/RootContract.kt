package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.model.AuthSession

sealed interface RootEvent

data class OnSessionStarted(
    val session: AuthSession,
) : RootEvent

class OnLogoutClick : RootEvent
