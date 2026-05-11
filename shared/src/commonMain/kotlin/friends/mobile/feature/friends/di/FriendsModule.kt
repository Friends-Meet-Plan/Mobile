package friends.mobile.feature.friends.di

import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.data.repository.FriendsRepositoryImpl
import friends.mobile.feature.friends.data.usecase.AcceptFriendRequestUseCaseImpl
import friends.mobile.feature.friends.data.usecase.CancelFriendRequestUseCaseImpl
import friends.mobile.feature.friends.data.usecase.GetFriendsUseCaseImpl
import friends.mobile.feature.friends.data.usecase.GetFriendStatusUseCaseImpl
import friends.mobile.feature.friends.data.usecase.GetIncomingFriendRequestsUseCaseImpl
import friends.mobile.feature.friends.data.usecase.GetOutgoingFriendRequestsUseCaseImpl
import friends.mobile.feature.friends.data.usecase.RejectFriendRequestUseCaseImpl
import friends.mobile.feature.friends.data.usecase.RemoveFriendUseCaseImpl
import friends.mobile.feature.friends.data.usecase.SearchUserUseCaseImpl
import friends.mobile.feature.friends.data.usecase.SendFriendRequestUseCaseImpl
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.AcceptFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.CancelFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import friends.mobile.feature.friends.domain.usecase.GetFriendStatusUseCase
import friends.mobile.feature.friends.domain.usecase.GetIncomingFriendRequestsUseCase
import friends.mobile.feature.friends.domain.usecase.GetOutgoingFriendRequestsUseCase
import friends.mobile.feature.friends.domain.usecase.RejectFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.RemoveFriendUseCase
import friends.mobile.feature.friends.domain.usecase.SearchUserUseCase
import friends.mobile.feature.friends.domain.usecase.SendFriendRequestUseCase
import friends.mobile.feature.friends.presentation.FriendProfileViewModel
import friends.mobile.feature.friends.presentation.FriendsViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for the friends feature.
 */
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

    factory<FriendsViewModel> { FriendsViewModel() }
    factory<FriendProfileViewModel> { FriendProfileViewModel() }
}
