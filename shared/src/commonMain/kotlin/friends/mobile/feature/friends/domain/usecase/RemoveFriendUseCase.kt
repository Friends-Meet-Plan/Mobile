package friends.mobile.feature.friends.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface RemoveFriendUseCase {
    suspend operator fun invoke(userId: String): ResultWrapper<Unit>
}
