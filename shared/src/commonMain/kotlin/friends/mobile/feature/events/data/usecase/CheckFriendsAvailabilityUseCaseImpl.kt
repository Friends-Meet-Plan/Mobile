package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.CheckFriendsAvailabilityUseCase
import friends.mobile.feature.friends.domain.model.User

internal class CheckFriendsAvailabilityUseCaseImpl(
    private val repository: EventsRepository,
) : CheckFriendsAvailabilityUseCase {
    override suspend fun invoke(date: String): ResultWrapper<List<User>> =
        repository.checkFriendsAvailability(date)
}
