package friends.mobile.feature.auth.domain.usecase

import friends.mobile.feature.auth.domain.model.AuthSession

interface LoginUseCase {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): AuthSession
}
