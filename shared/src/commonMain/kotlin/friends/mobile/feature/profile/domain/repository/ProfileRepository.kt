package friends.mobile.feature.profile.domain.repository

import friends.mobile.feature.profile.domain.model.Profile

interface ProfileRepository {
    suspend fun getMe(): Profile
}
