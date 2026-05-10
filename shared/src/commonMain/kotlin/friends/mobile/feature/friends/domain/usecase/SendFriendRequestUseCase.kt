package friends.mobile.feature.friends.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface SendFriendRequestUseCase {
    suspend operator fun invoke(friendId: String): ResultWrapper<Unit>
}
