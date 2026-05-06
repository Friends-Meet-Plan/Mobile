package friends.mobile.feature.auth.data.usecase

import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.feature.auth.domain.usecase.GetStoredSessionUseCase

internal class GetStoredSessionUseCaseImpl(
    private val repository: AuthRepository,
) : GetStoredSessionUseCase {
    override fun invoke(): AuthSession? = repository.getStoredSession()
}
