package friends.mobile.feature.profile.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.profile.domain.model.Profile

interface GetMeUseCase {
    suspend operator fun invoke(): ResultWrapper<Profile>
}
