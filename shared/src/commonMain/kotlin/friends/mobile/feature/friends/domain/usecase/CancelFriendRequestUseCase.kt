package friends.mobile.feature.friends.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface CancelFriendRequestUseCase {
    suspend operator fun invoke(requestId: String): ResultWrapper<Unit>
}
