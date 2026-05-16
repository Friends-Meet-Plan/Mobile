package friends.mobile.feature.wishplaces.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.wishplaces.data.mapper.WishPlaceMapper
import friends.mobile.feature.wishplaces.data.remote.WishPlacesApi
import friends.mobile.feature.wishplaces.data.remote.dto.CreateWishPlaceRequestDto
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import friends.mobile.feature.wishplaces.domain.repository.WishPlacesRepository

internal class WishPlacesRepositoryImpl(
    private val api: WishPlacesApi,
    private val mapper: WishPlaceMapper
) : WishPlacesRepository {

    override suspend fun getWishPlaces(userId: String): ResultWrapper<List<WishPlace>> {
        return safeApiCall {
            mapper.map(api.getWishPlaces(userId))
        }
    }

    override suspend fun createWishPlace(
        title: String,
        description: String?,
        location: String?,
        link: String?
    ): ResultWrapper<WishPlace> {
        return safeApiCall {
            val request = CreateWishPlaceRequestDto(
                title = title,
                description = description,
                location = location,
                link = link
            )
            mapper.map(api.createWishPlace(request))
        }
    }

    override suspend fun archiveWishPlace(id: String): ResultWrapper<Unit> {
        return safeApiCall {
            api.archiveWishPlace(id)
        }
    }
}
