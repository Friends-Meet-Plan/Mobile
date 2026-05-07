package friends.mobile.feature.auth.domain.usecase

import friends.mobile.feature.auth.domain.model.AuthSession

interface GetStoredSessionUseCase {
    operator fun invoke(): AuthSession?
}
