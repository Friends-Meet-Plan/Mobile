package friends.mobile.feature.profile.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.profile.domain.model.Profile
import friends.mobile.feature.profile.domain.repository.ProfileRepository
import friends.mobile.feature.profile.domain.usecase.UpdateProfileUseCase

internal class UpdateProfileUseCaseImpl(
    private val repository: ProfileRepository,
) : UpdateProfileUseCase {
    override suspend fun invoke(
        username: String?,
        avatarUrl: String?,
        bio: String?,
    ): ResultWrapper<Profile> = repository.updateProfile(username, avatarUrl, bio)
}
