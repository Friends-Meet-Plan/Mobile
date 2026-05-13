package friends.mobile.feature.event.data.remote

import friends.mobile.feature.event.data.remote.dto.CreateEventRequestDto
import friends.mobile.feature.event.data.remote.dto.EventResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * HTTP API wrapper for the event feature.
 *
 * Uses the authenticated HttpClient (named "auth") to make requests.
 * This client handles token refresh, 401 retry, and proactive token refresh.
 */
internal class EventApi(
    private val client: HttpClient,
) {

    /**
     * Create a new event.
     *
     * POST /events
     * Request: CreateEventRequestDto
     * Response (201): EventResponseDto
     *
     * @param request event creation request
     * @return EventResponseDto with event details and participants
     */
    suspend fun createEvent(request: CreateEventRequestDto): EventResponseDto =
        client.post("/events") {
            setBody(request)
        }.body()
}
