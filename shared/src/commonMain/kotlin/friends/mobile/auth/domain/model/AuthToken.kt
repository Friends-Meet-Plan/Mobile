package friends.mobile.auth.model

/** JWT access/refresh pair returned by login and refresh endpoints. */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
)
