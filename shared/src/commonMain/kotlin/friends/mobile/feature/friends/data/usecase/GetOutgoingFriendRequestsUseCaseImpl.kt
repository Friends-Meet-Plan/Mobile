package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.GetOutgoingFriendRequestsUseCase

internal class GetOutgoingFriendRequestsUseCaseImpl(
    private val repository: FriendsRepository
) : GetOutgoingFriendRequestsUseCase {
    override suspend fun invoke(): ResultWrapper<List<User>> {
        return repository.getOutgoingRequests()
    }
}
