package friends.mobile.auth.model

import kotlinx.serialization.Serializable

/** JWT access/refresh pair returned by login and refresh endpoints. */
@Serializable
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
)
