package friends.mobile.feature.calendar.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.calendar.data.mapper.CalendarDtoMapper
import friends.mobile.feature.calendar.data.remote.CalendarApi
import friends.mobile.feature.calendar.domain.model.CalendarResponse
import friends.mobile.feature.calendar.domain.repository.CalendarRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

internal class CalendarRepositoryImpl(
    private val api: CalendarApi,
    private val mapper: CalendarDtoMapper,
) : CalendarRepository {

    override suspend fun getBusyDays(userId: String): ResultWrapper<CalendarResponse> =
        safeApiCall {
            val dateRange = calculateDateRange()
            val response = api.getBusyDays(
                userId = userId,
                from = dateRange.first,
                to = dateRange.second,
            )
            mapper.toDomain(response)
        }

    private fun calculateDateRange(): Pair<String, String> {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val thirtyDaysFromNow = today.plus(kotlinx.datetime.DatePeriod(days = 30))

        return Pair(today.toString(), thirtyDaysFromNow.toString())
    }
}
