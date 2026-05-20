package friends.mobile.feature.main.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.events.data.remote.EventsApi
import friends.mobile.feature.events.data.remote.dto.EventResponseDto
import friends.mobile.feature.main.data.mapper.MainEventMapper
import friends.mobile.feature.main.domain.model.AvailabilityResult
import friends.mobile.feature.main.domain.model.MainEvent
import friends.mobile.feature.main.domain.repository.MainRepository

internal class MainRepositoryImpl(
    private val api: EventsApi,
    private val eventMapper: MainEventMapper,
) : MainRepository {

    override suspend fun getAcceptedEvents(): ResultWrapper<List<MainEvent>> =
        safeApiCall {
            val activeEvents = api.getActiveEvents()
            eventMapper.toDomainFromEventResponse(activeEvents)
        }

    override suspend fun getPendingEvents(): ResultWrapper<List<MainEvent>> =
        safeApiCall {
            val pendingEvents = api.getPendingEvents()
            eventMapper.toDomainFromEventResponse(pendingEvents)
        }

    override suspend fun getActiveAndPendingEvents(): ResultWrapper<Pair<List<MainEvent>, List<MainEvent>>> =
        safeApiCall {
            val activeEvents = api.getActiveEvents()
            val pendingEvents = api.getPendingEvents()
            Pair(
                eventMapper.toDomainFromEventResponse(activeEvents),
                eventMapper.toDomainFromEventResponse(pendingEvents)
            )
        }

    override suspend fun getArchivedEvents(): ResultWrapper<List<MainEvent>> =
        safeApiCall {
            val pastEvents = api.getEvents(scope = "past")
            eventMapper.toDomain(pastEvents.filter { it.status == "completed" })
        }

    override suspend fun checkUserAvailability(date: String): ResultWrapper<AvailabilityResult> =
        safeApiCall {
            val response = api.checkUserAvailability(date)
            if (response.isAvailable) {
                AvailabilityResult.Available
            } else {
                AvailabilityResult.Busy
            }
        }
}
