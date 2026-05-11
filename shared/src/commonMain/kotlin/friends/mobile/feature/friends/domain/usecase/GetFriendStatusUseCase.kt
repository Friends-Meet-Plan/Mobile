package friends.mobile.feature.friends.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.FriendStatus

interface GetFriendStatusUseCase {
    suspend operator fun invoke(userId: String): ResultWrapper<FriendStatus>
}
