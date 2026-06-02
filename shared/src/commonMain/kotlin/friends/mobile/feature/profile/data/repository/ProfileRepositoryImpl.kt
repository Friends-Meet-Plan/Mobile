package friends.mobile.feature.profile.data.repository

import friends.mobile.core.db.ProfileCacheStorage
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.profile.data.mapper.ProfileMapper
import friends.mobile.feature.profile.data.remote.ProfileApi
import friends.mobile.feature.profile.data.remote.dto.UpdateProfileRequestDto
import friends.mobile.feature.profile.domain.model.Profile
import friends.mobile.feature.profile.domain.repository.ProfileRepository

private const val NO_CONNECTION_ERROR_CODE = -1

internal class ProfileRepositoryImpl(
    private val api: ProfileApi,
    private val mapper: ProfileMapper,
    private val cacheStorage: ProfileCacheStorage,
) : ProfileRepository {

    override suspend fun getMe(): ResultWrapper<Profile> {
        val result = safeApiCall { mapper.mapToDomain(api.getMe()) }
        return when (result) {
            is ResultWrapper.Success -> {
                cacheStorage.saveProfile(result.data)
                result
            }
            is ResultWrapper.Error -> {
                if (result.error.code == NO_CONNECTION_ERROR_CODE) {
                    cacheStorage.getProfile()?.let { ResultWrapper.Success(it) } ?: result
                } else {
                    result
                }
            }
        }
    }

    override suspend fun updateProfile(
        username: String?,
        avatarUrl: String?,
        bio: String?
    ): ResultWrapper<Profile> {
        val result = safeApiCall {
            val body = UpdateProfileRequestDto(
                username = username,
                avatarUrl = avatarUrl,
                bio = bio
            )
            mapper.mapToDomain(api.patchMe(body))
        }
        if (result is ResultWrapper.Success) {
            cacheStorage.saveProfile(result.data)
        }
        return result
    }
}
