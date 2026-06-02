package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.GetArchivedEventsUseCase

internal class GetArchivedEventsUseCaseImpl(
    private val repository: EventsRepository,
) : GetArchivedEventsUseCase {

    override suspend fun invoke(): ResultWrapper<List<Event>> {
        return repository.getArchivedEvents()
    }
}
