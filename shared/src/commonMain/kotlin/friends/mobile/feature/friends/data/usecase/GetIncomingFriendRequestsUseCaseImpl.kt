package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.GetIncomingFriendRequestsUseCase

internal class GetIncomingFriendRequestsUseCaseImpl(
    private val repository: FriendsRepository
) : GetIncomingFriendRequestsUseCase {
    override suspend fun invoke(): ResultWrapper<List<User>> {
        return repository.getIncomingRequests()
    }
}
