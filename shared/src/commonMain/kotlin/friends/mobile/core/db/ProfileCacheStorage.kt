package friends.mobile.core.db

import com.russhwolf.settings.Settings
import friends.mobile.feature.profile.domain.model.Profile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val PROFILE_CACHE_KEY = "profile_cache"

interface ProfileCacheStorage {
    suspend fun getProfile(): Profile?
    suspend fun saveProfile(profile: Profile)
    suspend fun clearProfile()
}

class ProfileCacheStorageImpl(
    private val settings: Settings,
    private val json: Json,
) : ProfileCacheStorage {
    override suspend fun getProfile(): Profile? {
        val cached = settings.getStringOrNull(PROFILE_CACHE_KEY) ?: return null
        return try {
            json.decodeFromString(cached)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveProfile(profile: Profile) {
        val serialized = json.encodeToString(profile)
        settings.putString(PROFILE_CACHE_KEY, serialized)
    }

    override suspend fun clearProfile() {
        settings.remove(PROFILE_CACHE_KEY)
    }
}
