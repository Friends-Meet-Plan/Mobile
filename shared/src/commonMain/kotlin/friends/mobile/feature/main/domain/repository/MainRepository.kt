package friends.mobile.feature.main.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.main.domain.model.AvailabilityResult
import friends.mobile.feature.main.domain.model.MainEvent

interface MainRepository {
    suspend fun getAcceptedEvents(): ResultWrapper<List<MainEvent>>

    suspend fun getPendingEvents(): ResultWrapper<List<MainEvent>>

    suspend fun getActiveAndPendingEvents(): ResultWrapper<Pair<List<MainEvent>, List<MainEvent>>>

    suspend fun checkUserAvailability(date: String): ResultWrapper<AvailabilityResult>
}
