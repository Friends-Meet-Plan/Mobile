package friends.mobile.feature.auth.domain.model

/** Full on-device session: token pair + user profile. */
data class AuthSession(
    val token: AuthToken,
    val user: AuthUser,
)
