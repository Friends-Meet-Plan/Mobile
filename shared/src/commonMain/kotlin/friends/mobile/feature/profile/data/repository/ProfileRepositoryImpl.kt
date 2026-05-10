package friends.mobile.feature.profile.data.repository

import friends.mobile.feature.profile.data.mapper.ProfileMapper
import friends.mobile.feature.profile.data.remote.ProfileApi
import friends.mobile.feature.profile.domain.model.Profile
import friends.mobile.feature.profile.domain.repository.ProfileRepository

internal class ProfileRepositoryImpl(
    private val api: ProfileApi,
    private val mapper: ProfileMapper,
) : ProfileRepository {

    override suspend fun getMe(): Profile {
        return mapper.mapToDomain(api.getMe())
    }
}
