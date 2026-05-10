package friends.mobile.feature

import friends.mobile.feature.auth.di.authModule
import friends.mobile.feature.friends.di.friendsModule
import friends.mobile.feature.profile.di.profileModule
import org.koin.dsl.module

val featureModule = module {
    includes(
        authModule,
        friendsModule,
        profileModule,
    )
}
