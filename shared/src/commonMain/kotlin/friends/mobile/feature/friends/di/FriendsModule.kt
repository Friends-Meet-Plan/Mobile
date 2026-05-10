package friends.mobile.feature.friends.di

import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.data.repository.FriendsRepositoryImpl
import friends.mobile.feature.friends.data.usecase.AcceptFriendRequestUseCaseImpl
import friends.mobile.feature.friends.data.usecase.CancelFriendRequestUseCaseImpl
import friends.mobile.feature.friends.data.usecase.GetFriendsUseCaseImpl
import friends.mobile.feature.friends.data.usecase.GetIncomingFriendRequestsUseCaseImpl
import friends.mobile.feature.friends.data.usecase.GetOutgoingFriendRequestsUseCaseImpl
import friends.mobile.feature.friends.data.usecase.RejectFriendRequestUseCaseImpl
import friends.mobile.feature.friends.data.usecase.SearchUserUseCaseImpl
import friends.mobile.feature.friends.data.usecase.SendFriendRequestUseCaseImpl
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.AcceptFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.CancelFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import friends.mobile.feature.friends.domain.usecase.GetIncomingFriendRequestsUseCase
import friends.mobile.feature.friends.domain.usecase.GetOutgoingFriendRequestsUseCase
import friends.mobile.feature.friends.domain.usecase.RejectFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.SearchUserUseCase
import friends.mobile.feature.friends.domain.usecase.SendFriendRequestUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for the friends feature.
 *
 * Provides:
 *   - FriendsApi: HTTP wrapper using authenticated HttpClient
 *   - FriendsRepository: data access layer
 *   - GetFriendsUseCase: business logic for fetching friends
 *   - GetIncomingFriendRequestsUseCase: business logic for fetching incoming requests
 *   - GetOutgoingFriendRequestsUseCase: business logic for fetching outgoing requests
 *   - SendFriendRequestUseCase: send friend request
 *   - AcceptFriendRequestUseCase: accept friend request
 *   - RejectFriendRequestUseCase: reject friend request
 *   - CancelFriendRequestUseCase: cancel friend request
 *   - SearchUserUseCase: search for users by name/username
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

    single<UserDtoMapper> {
        UserDtoMapper()
    }

    single<FriendsRepository> {
        FriendsRepositoryImpl(api = get(), userDtoMapper = get())
    }

    factory<GetFriendsUseCase> { GetFriendsUseCaseImpl(repository = get()) }
    factory<GetIncomingFriendRequestsUseCase> { GetIncomingFriendRequestsUseCaseImpl(repository = get()) }
    factory<GetOutgoingFriendRequestsUseCase> { GetOutgoingFriendRequestsUseCaseImpl(repository = get()) }
    factory<SendFriendRequestUseCase> { SendFriendRequestUseCaseImpl(repository = get()) }
    factory<AcceptFriendRequestUseCase> { AcceptFriendRequestUseCaseImpl(repository = get()) }
    factory<RejectFriendRequestUseCase> { RejectFriendRequestUseCaseImpl(repository = get()) }
    factory<CancelFriendRequestUseCase> { CancelFriendRequestUseCaseImpl(repository = get()) }
    factory<SearchUserUseCase> { SearchUserUseCaseImpl(repository = get(), tokenStorage = get()) }
}
