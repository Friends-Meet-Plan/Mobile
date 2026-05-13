package friends.mobile.feature.event.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.event.domain.model.CreateEventRequest
import friends.mobile.feature.event.domain.model.Event

/**
 * Use case for creating a new event.
 *
 * Validates event data and delegates to the repository for persistence.
 */
interface CreateEventUseCase {
    suspend operator fun invoke(request: CreateEventRequest): ResultWrapper<Event>
}

class CreateEventUseCaseImpl(
    private val eventRepository: friends.mobile.feature.event.domain.repository.EventRepository,
) : CreateEventUseCase {

    override suspend fun invoke(request: CreateEventRequest): ResultWrapper<Event> {
        // Title is required
        if (request.title.isBlank()) {
            return ResultWrapper.Error(
                friends.mobile.core.domain.model.ApiError(
                    code = 400,
                    message = "Event title is required",
                )
            )
        }

        // Date must be in valid format (basic validation, full validation on backend)
        if (request.date.isBlank()) {
            return ResultWrapper.Error(
                friends.mobile.core.domain.model.ApiError(
                    code = 400,
                    message = "Event date is required",
                )
            )
        }

        return eventRepository.createEvent(request)
    }
}
