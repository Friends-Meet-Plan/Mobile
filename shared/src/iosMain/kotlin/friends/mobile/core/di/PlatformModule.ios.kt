package friends.mobile.core.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import friends.mobile.core.db.AppDatabase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Settings> {
        NSUserDefaultsSettings.Factory().create(
            name = get(named<QualifierSettingName>()),
        )
    }
    single<SqlDriver> {
        NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = get(named<QualifierDBName>()),
        )
    }
}
