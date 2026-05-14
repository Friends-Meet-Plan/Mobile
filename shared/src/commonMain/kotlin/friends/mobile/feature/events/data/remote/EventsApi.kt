package friends.mobile.feature.events.data.remote

import friends.mobile.feature.events.data.remote.dto.CheckAvailabilityResponseDto
import friends.mobile.feature.events.data.remote.dto.CreateEventRequestDto
import friends.mobile.feature.events.data.remote.dto.EventResponseDto
import friends.mobile.feature.main.data.remote.dto.EventListItemDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class EventsApi(
    private val client: HttpClient,
) {

    suspend fun createEvent(body: CreateEventRequestDto): EventResponseDto =
        client.post("/events") {
            setBody(body)
        }.body()

    suspend fun checkFriendsAvailability(date: String): CheckAvailabilityResponseDto =
        client.get("/events/check-availability") {
            parameter("date", date)
        }.body()

    suspend fun getEvents(scope: String = "upcoming"): List<EventListItemDto> =
        client.get("/events") {
            parameter("scope", scope)
        }.body()
}
