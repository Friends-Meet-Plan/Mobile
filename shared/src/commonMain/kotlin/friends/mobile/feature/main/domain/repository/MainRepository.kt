package friends.mobile.feature.main.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.main.domain.model.MainEvent

interface MainRepository {
    suspend fun getAcceptedEvents(): ResultWrapper<List<MainEvent>>
}
