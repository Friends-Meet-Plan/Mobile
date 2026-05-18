package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.DeclineEventUseCase

internal class DeclineEventUseCaseImpl(
    private val repository: EventsRepository,
) : DeclineEventUseCase {

    override suspend fun invoke(eventId: String): ResultWrapper<Unit> =
        repository.declineEvent(eventId)
}
