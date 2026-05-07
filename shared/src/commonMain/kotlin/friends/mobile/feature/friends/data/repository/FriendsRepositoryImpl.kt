package friends.mobile.feature.friends.data.repository

import friends.mobile.feature.friends.data.mapper.FriendMapper
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.domain.model.Friend
import friends.mobile.feature.friends.domain.repository.FriendsRepository

/**
 * Implementation of FriendsRepository.
 *
 * Handles API calls and data mapping.
 * Future: add caching layer if needed.
 */
internal class FriendsRepositoryImpl(
    private val api: FriendsApi,
    private val mapper: FriendMapper,
) : FriendsRepository {

    override suspend fun getFriends(): List<Friend> {
        val response = api.getFriends()
        return mapper.mapDtoListToDomain(response.friends)
    }
}
