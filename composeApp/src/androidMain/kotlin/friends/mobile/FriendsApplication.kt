package friends.mobile

import android.app.Application
import android.content.pm.ApplicationInfo
import friends.mobile.analytics.FirebaseAnalyticsServiceImpl
import friends.mobile.core.CommonKmp
import friends.mobile.core.analytics.AnalyticsService
import friends.mobile.core.config.createConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class FriendsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CommonKmp.initKoin(
            configuration = createConfiguration(
                isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
            ),
        ) {
            androidContext(this@FriendsApplication)
            androidLogger()
            modules(module {
                single<AnalyticsService> { FirebaseAnalyticsServiceImpl() }
            })
        }
    }
}
