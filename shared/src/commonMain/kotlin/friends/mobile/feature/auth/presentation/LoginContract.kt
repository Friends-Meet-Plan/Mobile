package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.model.AuthSession

sealed interface LoginAction

data class LoginSucceeded(
    val session: AuthSession,
) : LoginAction

sealed interface LoginEvent

data class OnLoginClick(
    val username: String,
    val password: String,
) : LoginEvent
