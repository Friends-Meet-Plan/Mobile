package friends.mobile.feature.auth.data.usecase

import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.feature.auth.domain.usecase.RegisterUseCase

internal class RegisterUseCaseImpl(
    private val repository: AuthRepository,
) : RegisterUseCase {
    override suspend fun invoke(username: String, password: String) {
        repository.register(username, password)
    }
}

