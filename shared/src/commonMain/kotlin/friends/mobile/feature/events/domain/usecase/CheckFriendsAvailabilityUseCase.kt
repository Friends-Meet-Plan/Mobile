package friends.mobile.feature.events.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.User

interface CheckFriendsAvailabilityUseCase {
    suspend operator fun invoke(date: String): ResultWrapper<List<User>>
}
