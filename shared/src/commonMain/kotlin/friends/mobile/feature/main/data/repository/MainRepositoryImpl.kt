package friends.mobile.feature.main.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.events.data.remote.EventsApi
import friends.mobile.feature.main.data.mapper.MainEventMapper
import friends.mobile.feature.main.domain.model.MainEvent
import friends.mobile.feature.main.domain.repository.MainRepository

internal class MainRepositoryImpl(
    private val api: EventsApi,
    private val eventMapper: MainEventMapper,
) : MainRepository {

    override suspend fun getAcceptedEvents(): ResultWrapper<List<MainEvent>> =
        safeApiCall {
            val response = api.getEvents(scope = "upcoming")
            eventMapper.toDomain(response)
        }

    override suspend fun getPendingEvents(): ResultWrapper<List<MainEvent>> =
        safeApiCall {
            val response = api.getPendingEvents()
            eventMapper.toDomainFromEventResponse(response)
        }

    override suspend fun getActiveAndPendingEvents(): ResultWrapper<Pair<List<MainEvent>, List<MainEvent>>> =
        safeApiCall {
            val activeResponse = api.getEvents(scope = "upcoming")
            val pendingResponse = api.getPendingEvents()
            Pair(
                eventMapper.toDomain(activeResponse),
                eventMapper.toDomainFromEventResponse(pendingResponse)
            )
        }
}
