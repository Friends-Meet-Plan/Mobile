package friends.mobile.feature.friends.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val id: String,
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    val bio: String? = null
)