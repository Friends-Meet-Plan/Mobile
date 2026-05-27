package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.AcceptFriendRequestUseCase

internal class AcceptFriendRequestUseCaseImpl(
    private val repository: FriendsRepository
) : AcceptFriendRequestUseCase {
    override suspend fun invoke(requestId: String): ResultWrapper<Unit> {
        return repository.acceptFriendRequest(requestId)
    }
}
