package friends.mobile.feature.wishplaces.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.wishplaces.domain.model.WishPlace

interface CreateWishPlaceUseCase {
    suspend operator fun invoke(
        title: String,
        description: String?,
        location: String?,
        link: String?
    ): ResultWrapper<WishPlace>
}
