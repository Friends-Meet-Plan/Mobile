package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.RemoveFriendUseCase

internal class RemoveFriendUseCaseImpl(
    private val repository: FriendsRepository,
) : RemoveFriendUseCase {
    override suspend fun invoke(userId: String): ResultWrapper<Unit> =
        repository.removeFriend(userId)
}
