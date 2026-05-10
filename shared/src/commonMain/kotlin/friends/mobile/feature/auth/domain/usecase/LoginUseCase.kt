package friends.mobile.feature.auth.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.auth.domain.model.AuthSession

interface LoginUseCase {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): ResultWrapper<AuthSession>
}
