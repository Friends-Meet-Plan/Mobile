package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.GetEventDetailUseCase

internal class GetEventDetailUseCaseImpl(
    private val repository: EventsRepository,
) : GetEventDetailUseCase {

    override suspend fun invoke(eventId: String): ResultWrapper<Event> =
        repository.getEventDetail(eventId)
}
