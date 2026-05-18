package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.eventdetail.domain.model.EventDetail
import friends.mobile.feature.eventdetail.domain.repository.EventDetailRepository
import friends.mobile.feature.events.domain.usecase.GetEventDetailUseCase

internal class GetEventDetailUseCaseImpl(
    private val repository: EventDetailRepository,
) : GetEventDetailUseCase {

    override suspend fun invoke(eventId: String): ResultWrapper<EventDetail> =
        repository.getEventDetail(eventId)
}
