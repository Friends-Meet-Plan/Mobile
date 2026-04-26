package friends.mobile.di

import friends.mobile.auth.di.authModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(appDeclaration: KoinApplication.() -> Unit = {}) {
    startKoin {
        appDeclaration()
        modules(platformModule, authModule)
    }
}
