package friends.mobile.feature.auth.domain.model

/** Full on-device session: token pair + user profile. */
data class AuthSession(
    val token: friends.mobile.feature.auth.domain.model.AuthToken,
    val user: friends.mobile.feature.auth.domain.model.AuthUser,
)
