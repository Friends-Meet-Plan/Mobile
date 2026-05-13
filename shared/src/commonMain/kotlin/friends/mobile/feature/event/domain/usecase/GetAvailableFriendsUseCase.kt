package friends.mobile.feature.event.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.User

/**
 * Use case for fetching friends who are free on a specific date.
 *
 * Used when creating an event to show which friends can be invited.
 */
interface GetAvailableFriendsUseCase {
    suspend operator fun invoke(date: String): ResultWrapper<List<User>>
}

class GetAvailableFriendsUseCaseImpl(
    private val eventRepository: friends.mobile.feature.event.domain.repository.EventRepository,
) : GetAvailableFriendsUseCase {

    override suspend fun invoke(date: String): ResultWrapper<List<User>> {
        return eventRepository.getAvailableFriendsForDate(date)
    }
}
