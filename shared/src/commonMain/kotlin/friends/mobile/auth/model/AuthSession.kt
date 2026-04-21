package friends.mobile.auth.model

import kotlinx.serialization.Serializable

/** Full on-device session: token pair + user profile. */
@Serializable
data class AuthSession(
    val token: AuthToken,
    val user: AuthUser,
)
