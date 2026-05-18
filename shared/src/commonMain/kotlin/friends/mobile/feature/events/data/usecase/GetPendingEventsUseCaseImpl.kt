package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.GetPendingEventsUseCase

internal class GetPendingEventsUseCaseImpl(
    private val repository: EventsRepository,
) : GetPendingEventsUseCase {

    override suspend fun invoke(): ResultWrapper<List<Event>> =
        repository.getPendingEvents()
}
