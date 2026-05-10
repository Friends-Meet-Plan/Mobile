package friends.mobile.feature.friends.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.User

interface GetIncomingFriendRequestsUseCase {
    suspend operator fun invoke(): ResultWrapper<List<User>>
}
