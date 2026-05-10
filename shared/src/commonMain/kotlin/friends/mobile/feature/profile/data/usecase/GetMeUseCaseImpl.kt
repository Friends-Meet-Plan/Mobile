package friends.mobile.feature.profile.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.profile.domain.model.Profile
import friends.mobile.feature.profile.domain.repository.ProfileRepository
import friends.mobile.feature.profile.domain.usecase.GetMeUseCase

internal class GetMeUseCaseImpl(
    private val repository: ProfileRepository
) : GetMeUseCase {
    override suspend fun invoke(): ResultWrapper<Profile> {
        return repository.getMe()
    }
}
