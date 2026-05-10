package friends.mobile.feature.auth.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface RegisterUseCase {
    suspend operator fun invoke(
        username: String,
        password: String,
        avatarUrl: String? = null,
        bio: String? = null,
    ): ResultWrapper<Unit>
}
