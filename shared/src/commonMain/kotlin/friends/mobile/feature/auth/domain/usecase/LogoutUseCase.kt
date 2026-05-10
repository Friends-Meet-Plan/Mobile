package friends.mobile.feature.auth.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface LogoutUseCase {
    suspend operator fun invoke(): ResultWrapper<Unit>
}
