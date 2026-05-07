package friends.mobile.feature.auth.data.usecase

import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.feature.auth.domain.usecase.LogoutUseCase

internal class LogoutUseCaseImpl(
    private val repository: AuthRepository,
) : LogoutUseCase {
    override suspend fun invoke() {
        repository.logout()
    }
}
