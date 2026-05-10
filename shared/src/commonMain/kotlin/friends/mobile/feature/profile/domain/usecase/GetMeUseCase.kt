package friends.mobile.feature.profile.domain.usecase

import friends.mobile.feature.profile.domain.model.Profile

interface GetMeUseCase {
    suspend operator fun invoke(): Profile
}
