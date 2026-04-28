package friends.mobile.feature

import friends.mobile.feature.auth.di.authModule
import org.koin.dsl.module

val featureModule = module {
    includes(authModule)
}
