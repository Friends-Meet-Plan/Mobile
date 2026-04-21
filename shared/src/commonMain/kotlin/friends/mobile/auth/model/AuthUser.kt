package friends.mobile.auth.model

/** Authenticated user profile returned by the login endpoint. */
data class AuthUser(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
)
