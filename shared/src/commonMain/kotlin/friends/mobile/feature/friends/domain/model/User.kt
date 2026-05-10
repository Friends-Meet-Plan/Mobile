package friends.mobile.feature.friends.domain.model

data class User(
    val id: String,
    val username: String,
    val bio: String?,
    val avatarUrl: String?,
)
