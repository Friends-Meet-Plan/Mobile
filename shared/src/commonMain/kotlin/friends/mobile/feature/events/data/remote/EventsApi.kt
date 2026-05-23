package friends.mobile.feature.events.data.remote

import friends.mobile.feature.events.data.remote.dto.CheckAvailabilityResponseDto
import friends.mobile.feature.events.data.remote.dto.CreateEventRequestDto
import friends.mobile.feature.events.data.remote.dto.CreateEventResponseDto
import friends.mobile.feature.events.data.remote.dto.EventResponseDto
import friends.mobile.feature.events.data.remote.dto.UserAvailabilityResponseDto
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

    suspend fun createEvent(body: CreateEventRequestDto): CreateEventResponseDto =
        client.post("/events") {
            setBody(body)
        }.body()

    suspend fun checkFriendsAvailability(date: String): CheckAvailabilityResponseDto =
        client.get("/events/check-availability") {
            parameter("date", date)
        }.body()

    suspend fun checkUserAvailability(date: String): UserAvailabilityResponseDto =
        client.get("/events/check-user-availability") {
            parameter("date", date)
        }.body()

    suspend fun getEvents(scope: String = "upcoming"): List<EventListItemDto> =
        client.get("/events") {
            parameter("scope", scope)
        }.body()

    suspend fun getEvent(eventId: String): EventResponseDto =
        client.get("/events/$eventId").body()

    suspend fun getActiveEvents(): List<EventResponseDto> =
        client.get("/events/active").body()

    suspend fun getWaitingEvents(): List<EventResponseDto> =
        client.get("/events/waiting").body()

    suspend fun getPendingEvents(): List<EventResponseDto> =
        client.get("/events/pending").body()

    suspend fun acceptEvent(eventId: String): Unit =
        client.post("/events/$eventId/accept").body()

    suspend fun declineEvent(eventId: String): Unit =
        client.post("/events/$eventId/decline").body()
}
