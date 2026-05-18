package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.AcceptEventUseCase

internal class AcceptEventUseCaseImpl(
    private val repository: EventsRepository,
) : AcceptEventUseCase {

    override suspend fun invoke(eventId: String): ResultWrapper<Unit> =
        repository.acceptEvent(eventId)
}
