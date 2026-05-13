package friends.mobile.feature.events.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.events.domain.usecase.CreateEventUseCase

internal class CreateEventUseCaseImpl(
    private val repository: EventsRepository,
) : CreateEventUseCase {
    override suspend fun invoke(
        title: String,
        description: String?,
        date: String,
        time: String?,
        location: String?,
        invitedFriendIds: List<String>,
    ): ResultWrapper<Event> = repository.createEvent(
        title = title,
        description = description,
        date = date,
        time = time,
        location = location,
        invitedFriendIds = invitedFriendIds,
    )
}
