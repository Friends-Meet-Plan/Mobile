package friends.mobile.feature.wishplaces.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.wishplaces.domain.model.WishPlace

interface WishPlacesRepository {
    suspend fun getWishPlaces(userId: String): ResultWrapper<List<WishPlace>>
    suspend fun createWishPlace(
        title: String,
        description: String?,
        location: String?,
        link: String?
    ): ResultWrapper<WishPlace>
    suspend fun archiveWishPlace(id: String): ResultWrapper<Unit>
}
