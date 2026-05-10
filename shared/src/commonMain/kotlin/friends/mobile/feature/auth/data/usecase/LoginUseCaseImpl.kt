package friends.mobile.feature.auth.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.feature.auth.domain.usecase.LoginUseCase

internal class LoginUseCaseImpl(
    private val repository: AuthRepository,
) : LoginUseCase {
    override suspend fun invoke(username: String, password: String): ResultWrapper<AuthSession> {
        return repository.login(username, password)
    }
}
