package friends.mobile.feature.friends.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.User

interface SearchUserUseCase {
    suspend operator fun invoke(query: String): ResultWrapper<List<User>>
}
