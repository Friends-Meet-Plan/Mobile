package friends.mobile

import android.app.Application
import android.content.pm.ApplicationInfo
import friends.mobile.core.CommonKmp
import friends.mobile.core.config.Configuration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FriendsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CommonKmp.initKoin(
            configuration = Configuration(
                isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
            ),
        ) {
            androidContext(this@FriendsApplication)
            androidLogger()
        }
    }
}
