package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.model.AuthSession

data class RootViewState(
    val session: friends.mobile.feature.auth.domain.model.AuthSession? = null,
)
