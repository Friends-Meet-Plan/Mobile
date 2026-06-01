package friends.mobile.core.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import friends.mobile.core.analytics.AnalyticsService
import friends.mobile.core.analytics.FirebaseAnalyticsServiceImpl
import friends.mobile.core.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Settings> {
        SharedPreferencesSettings(
            delegate = androidContext()
                .getSharedPreferences(
                    get(named<QualifierSettingName>()),
                    Context.MODE_PRIVATE,
                )
        )
    }
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = androidContext(),
            name = get(named<QualifierDBName>()),
        )
    }
    single<AnalyticsService> { FirebaseAnalyticsServiceImpl() }
}
