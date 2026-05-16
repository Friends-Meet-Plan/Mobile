package friends.mobile.feature.wishplaces.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.wishplaces.domain.model.WishPlace

interface GetWishPlacesUseCase {
    suspend operator fun invoke(userId: String): ResultWrapper<List<WishPlace>>
}
