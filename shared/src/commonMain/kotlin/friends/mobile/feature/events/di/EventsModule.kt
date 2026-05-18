package friends.mobile.feature.events.di

import friends.mobile.feature.events.data.remote.EventsApi
import friends.mobile.feature.events.data.repository.EventsRepositoryImpl
import friends.mobile.feature.events.data.usecase.AcceptEventUseCaseImpl
import friends.mobile.feature.events.data.usecase.CheckFriendsAvailabilityUseCaseImpl
import friends.mobile.feature.events.data.usecase.CreateEventUseCaseImpl
import friends.mobile.feature.events.data.usecase.DeclineEventUseCaseImpl
import friends.mobile.feature.events.data.usecase.GetEventDetailUseCaseImpl
import friends.mobile.feature.events.data.usecase.GetPendingEventsUseCaseImpl
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.AcceptEventUseCase
import friends.mobile.feature.events.domain.usecase.CheckFriendsAvailabilityUseCase
import friends.mobile.feature.events.domain.usecase.CreateEventUseCase
import friends.mobile.feature.events.domain.usecase.DeclineEventUseCase
import friends.mobile.feature.events.domain.usecase.GetEventDetailUseCase
import friends.mobile.feature.events.domain.usecase.GetPendingEventsUseCase
import friends.mobile.feature.events.presentation.CreateEventViewModel
import friends.mobile.feature.events.presentation.pendingevents.PendingEventViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val eventsModule = module {
    single<EventsApi> {
        EventsApi(client = get(named("auth")))
    }

    single<EventsRepository> {
        EventsRepositoryImpl(
            api = get(),
            userDtoMapper = get(),
            eventDetailMapper = get(),
        )
    }

    factory<CheckFriendsAvailabilityUseCase> {
        CheckFriendsAvailabilityUseCaseImpl(repository = get())
    }

    factory<CreateEventUseCase> {
        CreateEventUseCaseImpl(repository = get())
    }

    factory<GetPendingEventsUseCase> {
        GetPendingEventsUseCaseImpl(repository = get())
    }

    factory<GetEventDetailUseCase> {
        GetEventDetailUseCaseImpl(repository = get())
    }

    factory<AcceptEventUseCase> {
        AcceptEventUseCaseImpl(repository = get())
    }

    factory<DeclineEventUseCase> {
        DeclineEventUseCaseImpl(repository = get())
    }

    factory { (dateString: String) ->
        CreateEventViewModel(selectedDate = dateString)
    }

    factory {
        PendingEventViewModel()
    }
}
