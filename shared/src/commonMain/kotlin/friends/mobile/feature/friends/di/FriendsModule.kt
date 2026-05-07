package friends.mobile.feature.friends.di

import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.data.repository.FriendsRepositoryImpl
import friends.mobile.feature.friends.data.usecase.GetFriendsUseCaseImpl
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for the friends feature.
 *
 * Provides:
 *   - FriendsApi: HTTP wrapper using authenticated HttpClient
 *   - FriendsRepository: data access layer
 *   - GetFriendsUseCase: business logic for fetching friends
 *
 * Prerequisites in your Koin graph before including this module:
 *   - single<HttpClient>(named("auth")) { ... }  (authenticated client from authModule)
 *
 * Include in your FeatureModule:
 *   val featureModule = module {
 *       includes(authModule)
 *       includes(friendsModule)
 *   }
 */
val friendsModule = module {
    /**
     * FriendsApi uses the authenticated HttpClient (named "auth").
     * This client handles:
     *   - Proactive token refresh (before expiry)
     *   - 401 reactive retry with refresh
     *   - Token storage/rotation
     */
    single<FriendsApi> {
        FriendsApi(client = get(named("auth")))
    }

    single<FriendsRepository> {
        FriendsRepositoryImpl(api = get())
    }

    factory<GetFriendsUseCase> { GetFriendsUseCaseImpl(repository = get()) }
}
