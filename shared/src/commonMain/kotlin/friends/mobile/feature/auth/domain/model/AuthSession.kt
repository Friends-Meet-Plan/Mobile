package friends.mobile.feature.auth.domain.model

data class AuthSession(
    val token: AuthToken,
    val user: AuthUser,
)
