package friends.mobile.feature.friends.data.remote

import friends.mobile.feature.friends.data.remote.dto.GetFriendsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * HTTP API wrapper for the friends feature.
 *
 * Uses the authenticated HttpClient (named "auth") to make requests.
 * This client handles token refresh, 401 retry, and proactive token refresh.
 */
internal class FriendsApi(
    private val client: HttpClient,
) {

    /**
     * Fetch all available friends from the backend.
     *
     * GET /friends
     * Response: { "friends": [ { "id", "username", "avatar_url", "bio" }, ... ] }
     */
    suspend fun getFriends(): GetFriendsResponseDto =
        client.get("/friends").body()
}
