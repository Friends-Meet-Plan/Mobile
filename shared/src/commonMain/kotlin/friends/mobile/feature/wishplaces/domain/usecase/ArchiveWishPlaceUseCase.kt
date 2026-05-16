package friends.mobile.feature.wishplaces.domain.usecase

import friends.mobile.core.domain.model.ResultWrapper

interface ArchiveWishPlaceUseCase {
    suspend operator fun invoke(id: String): ResultWrapper<Unit>
}
