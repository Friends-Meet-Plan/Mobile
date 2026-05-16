package friends.mobile.feature.calendar.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.calendar.domain.model.CalendarResponse
import friends.mobile.feature.calendar.domain.repository.CalendarRepository
import friends.mobile.feature.calendar.domain.usecase.GetBusyDaysUseCase

internal class GetBusyDaysUseCaseImpl(
    private val repository: CalendarRepository,
) : GetBusyDaysUseCase {

    override suspend fun invoke(userId: String): ResultWrapper<CalendarResponse> =
        repository.getBusyDays(userId)
}
