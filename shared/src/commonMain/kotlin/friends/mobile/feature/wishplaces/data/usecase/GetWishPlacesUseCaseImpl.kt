package friends.mobile.feature.wishplaces.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import friends.mobile.feature.wishplaces.domain.model.WishPlaceStatus
import friends.mobile.feature.wishplaces.domain.repository.WishPlacesRepository
import friends.mobile.feature.wishplaces.domain.usecase.GetWishPlacesUseCase

internal class GetWishPlacesUseCaseImpl(
    private val repository: WishPlacesRepository
) : GetWishPlacesUseCase {
    override suspend fun invoke(userId: String): ResultWrapper<List<WishPlace>> {
        return when (val result = repository.getWishPlaces(userId)) {
            is ResultWrapper.Success -> {
                val filteredPlaces = result.data.filter { it.status != WishPlaceStatus.ARCHIVED }
                ResultWrapper.Success(filteredPlaces)
            }
            is ResultWrapper.Error -> result
        }
    }
}
