package friends.mobile.auth.model

import kotlinx.serialization.Serializable

/** Authenticated user profile returned by the login endpoint. */
@Serializable
data class AuthUser(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
)
