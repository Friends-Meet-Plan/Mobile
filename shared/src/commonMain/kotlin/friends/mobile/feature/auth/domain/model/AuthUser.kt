package friends.mobile.feature.auth.domain.model

data class AuthUser(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
)
