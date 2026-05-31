package friends.mobile.core.db.di

import com.russhwolf.settings.Settings
import friends.mobile.core.db.ProfileCacheStorage
import friends.mobile.core.db.ProfileCacheStorageImpl
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val databaseModule = module {
    single<ProfileCacheStorage> {
        ProfileCacheStorageImpl(get<Settings>(), get<Json>())
    }
}
