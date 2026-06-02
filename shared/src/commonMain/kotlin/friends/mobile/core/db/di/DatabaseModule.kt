package friends.mobile.core.db.di

import app.cash.sqldelight.db.SqlDriver
import friends.mobile.core.db.AppDatabase
import friends.mobile.core.db.ProfileCacheStorage
import friends.mobile.core.db.ProfileCacheStorageImpl
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        AppDatabase(driver = get<SqlDriver>())
    }
    single<ProfileCacheStorage> {
        ProfileCacheStorageImpl(database = get())
    }
}
