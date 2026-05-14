package friends.mobile.feature.eventdetail.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.eventdetail.data.mapper.EventDetailMapper
import friends.mobile.feature.eventdetail.domain.model.EventDetail
import friends.mobile.feature.eventdetail.domain.repository.EventDetailRepository
import friends.mobile.feature.events.data.remote.EventsApi

internal class EventDetailRepositoryImpl(
    private val api: EventsApi,
    private val mapper: EventDetailMapper,
) : EventDetailRepository {

    override suspend fun getEventDetail(eventId: String): ResultWrapper<EventDetail> =
        safeApiCall {
            val response = api.getEvent(eventId)
            mapper.toDomain(response)
        }
}
