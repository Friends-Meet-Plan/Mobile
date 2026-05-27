package friends.mobile.core.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import friends.mobile.core.analytics.AnalyticsService
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private class NoOpAnalyticsService : AnalyticsService {
    override fun logScreenView(screenName: String) {}
    override fun logEvent(name: String, params: Map<String, String>) {}
}

actual val platformModule: Module = module {
    single<Settings> {
        NSUserDefaultsSettings.Factory().create(
            name = get(named<QualifierSettingName>()),
        )
    }
    single<AnalyticsService> { NoOpAnalyticsService() }
}
