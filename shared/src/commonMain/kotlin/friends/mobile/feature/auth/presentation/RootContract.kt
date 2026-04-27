package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.model.AuthSession

sealed interface RootEvent

data class OnSessionStarted(
    val session: friends.mobile.feature.auth.domain.model.AuthSession,
) : friends.mobile.feature.auth.presentation.RootEvent

class OnLogoutClick : friends.mobile.feature.auth.presentation.RootEvent
