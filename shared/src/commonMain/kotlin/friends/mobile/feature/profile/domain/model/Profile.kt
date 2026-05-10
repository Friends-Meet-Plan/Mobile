package friends.mobile.feature.profile.domain.model

data class Profile(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
)
