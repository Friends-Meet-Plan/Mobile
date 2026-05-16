package friends.mobile.feature.calendar.data.remote

import friends.mobile.feature.calendar.data.remote.dto.CalendarResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class CalendarApi(
    private val client: HttpClient,
) {

    suspend fun getBusyDays(
        userId: String,
        from: String,
        to: String,
    ): CalendarResponseDto =
        client.get("/users/$userId/calendar") {
            parameter("from", from)
            parameter("to", to)
        }.body()
}
