package friends.mobile

import android.app.Application
import friends.mobile.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FriendsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FriendsApplication)
            androidLogger()
        }
    }
}
