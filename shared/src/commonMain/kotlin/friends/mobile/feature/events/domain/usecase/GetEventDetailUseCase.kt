package friends.mobile.feature.events.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.eventdetail.domain.model.EventDetail

interface GetEventDetailUseCase {
    suspend operator fun invoke(eventId: String): ResultWrapper<EventDetail>
}
