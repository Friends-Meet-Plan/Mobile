package friends.mobile.feature.event.data.repository

import friends.mobile.core.domain.model.ApiError
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.event.data.mapper.EventDtoMapper
import friends.mobile.feature.event.data.remote.EventApi
import friends.mobile.feature.event.data.remote.dto.CreateEventRequestDto
import friends.mobile.feature.event.domain.model.CreateEventRequest
import friends.mobile.feature.event.domain.model.Event
import friends.mobile.feature.event.domain.repository.EventRepository
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode

/**
 * Implementation of EventRepository.
 * Handles network communication with the backend and maps DTOs to domain models.
 */
internal class
EventRepositoryImpl(
    private val eventApi: EventApi,
    private val eventDtoMapper: EventDtoMapper,
    private val getFriendsUseCase: GetFriendsUseCase,
) : EventRepository {

    override suspend fun createEvent(request: CreateEventRequest): ResultWrapper<Event> {
        return try {
            val dto = CreateEventRequestDto(
                date = request.date,
                title = request.title,
                description = request.description,
                location = request.location,
                participantIds = request.participantIds,
                wishPlaceId = request.wishPlaceId,
            )

            val response = eventApi.createEvent(dto)
            val event = eventDtoMapper.map(response)
            ResultWrapper.Success(event)
        } catch (e: ClientRequestException) {
            val statusCode = e.response.status.value
            val message = when (statusCode) {
                400 -> "Invalid event data. Please check all fields."
                403 -> "You can only invite accepted friends to events."
                409 -> "One or more participants are busy on the selected date."
                else -> "Failed to create event: ${e.message}"
            }
            ResultWrapper.Error(ApiError(code = statusCode, message = message))
        } catch (e: ServerResponseException) {
            ResultWrapper.Error(ApiError(code = 500, message = "Server error. Please try again later."))
        } catch (e: SocketTimeoutException) {
            ResultWrapper.Error(ApiError(code = -1, message = "Network timeout. Please check your connection."))
        } catch (e: Exception) {
            ResultWrapper.Error(ApiError(code = -1, message = "An error occurred: ${e.message}"))
        }
    }

    override suspend fun getAvailableFriendsForDate(date: String): ResultWrapper<List<User>> {
        return try {
            // Get all friends
            val friendsResult = getFriendsUseCase()

            when (friendsResult) {
                is ResultWrapper.Success -> {
                    // Filter out friends who are busy on the given date
                    // For now, return all friends - the frontend can filter based on availability
                    // A more sophisticated approach would make individual /calendar/is_busy calls
                    // per friend, but that's inefficient. Ideally, there's a batch endpoint.
                    ResultWrapper.Success(friendsResult.data)
                }
                is ResultWrapper.Error -> friendsResult
            }
        } catch (e: SocketTimeoutException) {
            ResultWrapper.Error(ApiError(code = -1, message = "Network timeout. Please check your connection."))
        } catch (e: Exception) {
            ResultWrapper.Error(ApiError(code = -1, message = "Failed to load friends: ${e.message}"))
        }
    }
}
