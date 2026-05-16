package friends.mobile.feature.events.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface CreateEventUseCase {
    suspend operator fun invoke(
        title: String,
        description: String?,
        date: String,
        time: String?,
        location: String?,
        invitedFriendIds: List<String>,
    ): ResultWrapper<String>
}
