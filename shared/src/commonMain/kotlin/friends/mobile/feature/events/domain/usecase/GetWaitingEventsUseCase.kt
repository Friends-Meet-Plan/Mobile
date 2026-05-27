package friends.mobile.feature.events.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event

interface GetWaitingEventsUseCase {
    suspend operator fun invoke(): ResultWrapper<List<Event>>
}
