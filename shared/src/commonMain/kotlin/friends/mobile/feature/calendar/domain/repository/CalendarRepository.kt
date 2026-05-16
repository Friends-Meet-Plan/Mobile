package friends.mobile.feature.calendar.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.calendar.domain.model.CalendarResponse

interface CalendarRepository {
    suspend fun getBusyDays(userId: String): ResultWrapper<CalendarResponse>
}
