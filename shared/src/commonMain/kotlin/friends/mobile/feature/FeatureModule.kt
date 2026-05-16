package friends.mobile.feature

import friends.mobile.feature.auth.di.authModule
import friends.mobile.feature.calendar.di.calendarModule
import friends.mobile.feature.eventdetail.di.eventDetailModule
import friends.mobile.feature.events.di.eventsModule
import friends.mobile.feature.friends.di.friendsModule
import friends.mobile.feature.main.di.mainModule
import friends.mobile.feature.profile.di.profileModule
import friends.mobile.feature.wishplaces.di.wishPlacesModule
import org.koin.dsl.module

val featureModule = module {
    includes(
        authModule,
        friendsModule,
        profileModule,
        eventsModule,
        eventDetailModule,
        mainModule,
        wishPlacesModule,
        calendarModule,
    )
}
