package friends.mobile.feature.wishplaces.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.wishplaces.domain.repository.WishPlacesRepository
import friends.mobile.feature.wishplaces.domain.usecase.ArchiveWishPlaceUseCase

internal class ArchiveWishPlaceUseCaseImpl(
    private val repository: WishPlacesRepository
) : ArchiveWishPlaceUseCase {
    override suspend fun invoke(id: String): ResultWrapper<Unit> {
        return repository.archiveWishPlace(id)
    }
}
