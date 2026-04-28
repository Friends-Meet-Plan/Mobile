package friends.mobile.core.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
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
}
