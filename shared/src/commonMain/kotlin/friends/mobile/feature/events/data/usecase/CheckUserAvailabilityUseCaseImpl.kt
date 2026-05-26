package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.CheckUserAvailabilityUseCase

internal class CheckUserAvailabilityUseCaseImpl(
    private val repository: EventsRepository,
) : CheckUserAvailabilityUseCase {
    override suspend fun invoke(date: String): ResultWrapper<Boolean> =
        repository.checkUserAvailability(date)
}
