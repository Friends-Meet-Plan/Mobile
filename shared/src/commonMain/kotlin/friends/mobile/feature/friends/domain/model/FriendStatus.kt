package friends.mobile.feature.friends.domain.model

import friends.mobile.feature.friends.presentation.friendsProfile.FriendshipStatus

data class FriendStatus(
    val user: User,
    val status: FriendshipStatus,
)
