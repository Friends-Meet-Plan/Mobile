package friends.mobile.feature.events.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface AcceptEventUseCase {
    suspend operator fun invoke(eventId: String): ResultWrapper<Unit>
}
