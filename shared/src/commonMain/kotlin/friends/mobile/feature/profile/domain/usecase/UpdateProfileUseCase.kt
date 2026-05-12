package friends.mobile.feature.profile.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.profile.domain.model.Profile

interface UpdateProfileUseCase {
    suspend operator fun invoke(
        username: String? = null,
        avatarUrl: String? = null,
        bio: String? = null,
    ): ResultWrapper<Profile>
}
