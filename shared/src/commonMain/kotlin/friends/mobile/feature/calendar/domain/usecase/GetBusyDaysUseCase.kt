package friends.mobile.feature.calendar.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.calendar.domain.model.CalendarResponse

interface GetBusyDaysUseCase {
    suspend operator fun invoke(userId: String): ResultWrapper<CalendarResponse>
}
