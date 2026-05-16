package friends.mobile.feature.friends.di

import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.data.repository.FriendsRepositoryImpl
import friends.mobile.feature.friends.data.usecase.*
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.*
import friends.mobile.feature.friends.presentation.friendsProfile.FriendProfileViewModel
import friends.mobile.feature.friends.presentation.friends.FriendsViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val friendsModule = module {
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
    factory<GetFriendStatusUseCase> { GetFriendStatusUseCaseImpl(repository = get()) }
    factory<SendFriendRequestUseCase> { SendFriendRequestUseCaseImpl(repository = get()) }
    factory<AcceptFriendRequestUseCase> { AcceptFriendRequestUseCaseImpl(repository = get()) }
    factory<RejectFriendRequestUseCase> { RejectFriendRequestUseCaseImpl(repository = get()) }
    factory<RemoveFriendUseCase> { RemoveFriendUseCaseImpl(repository = get()) }
    factory<CancelFriendRequestUseCase> { CancelFriendRequestUseCaseImpl(repository = get()) }
    factory<SearchUserUseCase> {
        SearchUserUseCaseImpl(
            repository = get(),
            getStoredSessionUseCase = get(),
        )
    }
    factory { FriendsViewModel() }
    factory { FriendProfileViewModel() }
}
