package friends.mobile.feature.events.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface CheckUserAvailabilityUseCase {
    suspend operator fun invoke(date: String): ResultWrapper<Boolean>
}
