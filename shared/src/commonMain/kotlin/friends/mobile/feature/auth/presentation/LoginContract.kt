package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.model.AuthSession

sealed interface LoginAction

data class LoginSucceeded(
    val session: friends.mobile.feature.auth.domain.model.AuthSession,
) : friends.mobile.feature.auth.presentation.LoginAction

sealed interface LoginEvent

data class OnLoginClick(
    val username: String,
    val password: String,
) : friends.mobile.feature.auth.presentation.LoginEvent
