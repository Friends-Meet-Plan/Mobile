package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.auth.domain.usecase.GetStoredSessionUseCase
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.SearchUserUseCase

internal class SearchUserUseCaseImpl(
    private val repository: FriendsRepository,
    private val getStoredSessionUseCase: GetStoredSessionUseCase,
) : SearchUserUseCase {

    override suspend fun invoke(query: String): ResultWrapper<List<User>> {
        val currentUserId = getStoredSessionUseCase()?.user?.id

        return repository.searchUsers(query).map { users ->
            if (currentUserId != null) {
                users.filter { it.id != currentUserId }
            } else {
                users
            }
        }
    }
}
