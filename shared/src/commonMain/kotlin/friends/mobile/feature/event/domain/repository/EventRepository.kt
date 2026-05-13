package friends.mobile.feature.event.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.event.domain.model.CreateEventRequest
import friends.mobile.feature.event.domain.model.Event
import friends.mobile.feature.friends.domain.model.User

/**
 * Repository interface for event-related operations.
 * Abstracts data sources and provides domain models to use cases.
 */
interface EventRepository {

    /**
     * Create a new event with the given details.
     *
     * @param request event creation request containing date, title, participants, etc.
     * @return Result containing the created event or an error
     */
    suspend fun createEvent(request: CreateEventRequest): ResultWrapper<Event>

    /**
     * Get all accepted friends of the current user who are free on the specified date.
     *
     * @param date date in "YYYY-MM-DD" format
     * @return Result containing list of available friends or an error
     */
    suspend fun getAvailableFriendsForDate(date: String): ResultWrapper<List<User>>
}
