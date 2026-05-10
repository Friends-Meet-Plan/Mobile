package friends.mobile.feature.friends.domain.usecase

import friends.mobile.feature.friends.domain.repository.FriendsRepository

class RemoveFriendUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(userId: String) = repository.removeFriend(userId)
}
