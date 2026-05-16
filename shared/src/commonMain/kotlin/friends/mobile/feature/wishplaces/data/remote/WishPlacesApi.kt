package friends.mobile.feature.wishplaces.data.remote

import friends.mobile.feature.wishplaces.data.remote.dto.CreateWishPlaceRequestDto
import friends.mobile.feature.wishplaces.data.remote.dto.WishPlaceDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class WishPlacesApi(private val client: HttpClient) {

    suspend fun getWishPlaces(userId: String): List<WishPlaceDto> {
        return client.get("/wish-places") {
            parameter("user_id", userId)
        }.body()
    }

    suspend fun createWishPlace(request: CreateWishPlaceRequestDto): WishPlaceDto {
        return client.post("/wish-places") {
            setBody(request)
        }.body()
    }

    suspend fun archiveWishPlace(id: String) {
        client.delete("/wish-places/$id")
    }
}
