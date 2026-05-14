package friends.mobile.feature.events.di

import friends.mobile.feature.events.data.mapper.EventDtoMapper
import friends.mobile.feature.events.data.remote.EventsApi
import friends.mobile.feature.events.data.repository.EventsRepositoryImpl
import friends.mobile.feature.events.data.usecase.CheckFriendsAvailabilityUseCaseImpl
import friends.mobile.feature.events.data.usecase.CreateEventUseCaseImpl
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.CheckFriendsAvailabilityUseCase
import friends.mobile.feature.events.domain.usecase.CreateEventUseCase
import friends.mobile.feature.events.presentation.CreateEventViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val eventsModule = module {
    single<EventsApi> {
        EventsApi(client = get(named("auth")))
    }

    single<EventDtoMapper> {
        EventDtoMapper()
    }

    single<EventsRepository> {
        EventsRepositoryImpl(
            api = get(),
            eventDtoMapper = get(),
            userDtoMapper = get(),
        )
    }

    factory<CheckFriendsAvailabilityUseCase> {
        CheckFriendsAvailabilityUseCaseImpl(repository = get())
    }

    factory<CreateEventUseCase> {
        CreateEventUseCaseImpl(repository = get())
    }

    factory { (dateString: String) ->
        CreateEventViewModel(selectedDate = dateString)
    }
}
