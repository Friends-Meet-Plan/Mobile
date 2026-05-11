package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.FriendStatus
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.GetFriendStatusUseCase

internal class GetFriendStatusUseCaseImpl(
    private val repository: FriendsRepository,
) : GetFriendStatusUseCase {
    override suspend fun invoke(userId: String): ResultWrapper<FriendStatus> =
        repository.getFriendStatus(userId)
}
