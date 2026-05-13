package friends.mobile.feature.event.di

import friends.mobile.feature.event.data.mapper.EventDtoMapper
import friends.mobile.feature.event.data.remote.EventApi
import friends.mobile.feature.event.data.repository.EventRepositoryImpl
import friends.mobile.feature.event.domain.repository.EventRepository
import friends.mobile.feature.event.domain.usecase.CreateEventUseCase
import friends.mobile.feature.event.domain.usecase.CreateEventUseCaseImpl
import friends.mobile.feature.event.domain.usecase.GetAvailableFriendsUseCase
import friends.mobile.feature.event.domain.usecase.GetAvailableFriendsUseCaseImpl
import friends.mobile.feature.event.presentation.create.CreateEventViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for the event feature.
 *
 * Wires all event-related dependencies:
 * - API clients
 * - Mappers
 * - Repositories
 * - Use cases
 * - ViewModels
 */
val eventModule = module {
    // Remote API client
    // Uses the authenticated HttpClient to automatically handle token refresh
    single<EventApi> {
        EventApi(client = get(named("auth")))
    }

    // Data mapping
    single<EventDtoMapper> {
        EventDtoMapper()
    }

    // Repository implementation
    single<EventRepository> {
        EventRepositoryImpl(
            eventApi = get(),
            eventDtoMapper = get(),
            getFriendsUseCase = get(),
        )
    }

    // Use cases
    factory<CreateEventUseCase> {
        CreateEventUseCaseImpl(eventRepository = get())
    }

    factory<GetAvailableFriendsUseCase> {
        GetAvailableFriendsUseCaseImpl(eventRepository = get())
    }

    // ViewModels
    factory<CreateEventViewModel> { parameters ->
        CreateEventViewModel(selectedDate = parameters.getOrNull(0))
    }
}
