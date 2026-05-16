package friends.mobile.feature.calendar.di

import friends.mobile.feature.calendar.data.mapper.CalendarDtoMapper
import friends.mobile.feature.calendar.data.remote.CalendarApi
import friends.mobile.feature.calendar.data.repository.CalendarRepositoryImpl
import friends.mobile.feature.calendar.data.usecase.GetBusyDaysUseCaseImpl
import friends.mobile.feature.calendar.domain.repository.CalendarRepository
import friends.mobile.feature.calendar.domain.usecase.GetBusyDaysUseCase
import friends.mobile.feature.calendar.presentation.BusyDaysViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val calendarModule = module {
    single<CalendarApi> {
        CalendarApi(client = get(named("auth")))
    }

    single<CalendarDtoMapper> {
        CalendarDtoMapper
    }

    single<CalendarRepository> {
        CalendarRepositoryImpl(
            api = get(),
            mapper = get(),
        )
    }

    factory<GetBusyDaysUseCase> {
        GetBusyDaysUseCaseImpl(repository = get())
    }

    factory { (userId: String) ->
        BusyDaysViewModel(userId = userId)
    }
}
