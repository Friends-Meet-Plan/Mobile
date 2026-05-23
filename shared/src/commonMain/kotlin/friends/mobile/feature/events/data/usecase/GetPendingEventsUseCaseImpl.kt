package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.model.ParticipationStatus
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.GetPendingEventsUseCase

internal class GetPendingEventsUseCaseImpl(
    private val repository: EventsRepository,
) : GetPendingEventsUseCase {

    override suspend fun invoke(): ResultWrapper<List<Event>> {
        val result = repository.getWaitingEvents()
        if (result is ResultWrapper.Error) return result

        val events = (result as ResultWrapper.Success).data
        val filtered = events.filter { event ->
            event.participants.any { participant ->
                participant.status == ParticipationStatus.INVITED
            }
        }
        return ResultWrapper.Success(filtered)
    }
}
