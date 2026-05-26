package friends.mobile.feature.events.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event

interface GetEventDetailUseCase {
    suspend operator fun invoke(eventId: String): ResultWrapper<Event>
}
