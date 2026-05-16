package friends.mobile.feature.eventdetail.di

import friends.mobile.feature.eventdetail.data.mapper.EventDetailMapper
import friends.mobile.feature.eventdetail.data.repository.EventDetailRepositoryImpl
import friends.mobile.feature.eventdetail.domain.repository.EventDetailRepository
import friends.mobile.feature.eventdetail.presentation.EventDetailViewModel
import org.koin.dsl.module

val eventDetailModule = module {
    single<EventDetailMapper> {
        EventDetailMapper()
    }

    single<EventDetailRepository> {
        EventDetailRepositoryImpl(
            api = get(),
            mapper = get(),
        )
    }

    factory { (eventId: String) ->
        EventDetailViewModel(eventId = eventId)
    }
}
