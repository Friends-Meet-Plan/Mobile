package friends.mobile.feature.friends.domain.usecase

import friends.mobile.feature.friends.domain.model.Friend

/**
 * Public interface for the get friends use case.
 */
interface GetFriendsUseCase {

    /**
     * Fetch all available friends.
     */
    suspend operator fun invoke(page: Int): List<Friend>
}
