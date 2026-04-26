package friends.mobile.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences("friends_auth", Context.MODE_PRIVATE)
        )
    }
}
