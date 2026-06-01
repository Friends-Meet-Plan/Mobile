package friends.mobile.core.db

import friends.mobile.feature.profile.domain.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ProfileCacheStorage {
    suspend fun getProfile(): Profile?
    suspend fun saveProfile(profile: Profile)
    suspend fun clearProfile()
}

internal class ProfileCacheStorageImpl(
    private val database: AppDatabase,
) : ProfileCacheStorage {

    override suspend fun getProfile(): Profile? = withContext(Dispatchers.Default) {
        database.profileQueries.getProfile().executeAsOneOrNull()?.let {
            Profile(
                id = it.id,
                username = it.username,
                avatarUrl = it.avatar_url,
                bio = it.bio,
            )
        }
    }

    override suspend fun saveProfile(profile: Profile) = withContext(Dispatchers.Default) {
        database.profileQueries.upsertProfile(
            id = profile.id,
            username = profile.username,
            avatarUrl = profile.avatarUrl,
            bio = profile.bio,
        )
    }

    override suspend fun clearProfile() = withContext(Dispatchers.Default) {
        database.profileQueries.clearProfile()
    }
}
