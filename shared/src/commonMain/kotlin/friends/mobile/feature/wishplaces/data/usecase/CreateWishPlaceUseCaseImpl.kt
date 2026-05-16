package friends.mobile.feature.wishplaces.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import friends.mobile.feature.wishplaces.domain.repository.WishPlacesRepository
import friends.mobile.feature.wishplaces.domain.usecase.CreateWishPlaceUseCase

internal class CreateWishPlaceUseCaseImpl(
    private val repository: WishPlacesRepository
) : CreateWishPlaceUseCase {
    override suspend fun invoke(
        title: String,
        description: String?,
        location: String?,
        link: String?
    ): ResultWrapper<WishPlace> {
        return repository.createWishPlace(title, description, location, link)
    }
}
