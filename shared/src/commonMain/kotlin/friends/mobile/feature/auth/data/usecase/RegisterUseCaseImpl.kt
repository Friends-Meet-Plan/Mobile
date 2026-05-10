package friends.mobile.feature.auth.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.feature.auth.domain.usecase.RegisterUseCase

internal class RegisterUseCaseImpl(
    private val repository: AuthRepository,
) : RegisterUseCase {
    override suspend fun invoke(
        username: String,
        password: String,
        avatarUrl: String?,
        bio: String?,
    ): ResultWrapper<Unit> {
        return repository.register(username, password, avatarUrl, bio)
    }
}
