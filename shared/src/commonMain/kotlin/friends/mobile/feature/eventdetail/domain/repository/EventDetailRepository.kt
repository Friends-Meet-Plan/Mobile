package friends.mobile.feature.eventdetail.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.eventdetail.domain.model.EventDetail

interface EventDetailRepository {
    suspend fun getEventDetail(eventId: String): ResultWrapper<EventDetail>
}
