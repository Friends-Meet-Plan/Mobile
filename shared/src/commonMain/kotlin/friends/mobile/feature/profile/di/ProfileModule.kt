package friends.mobile.feature.profile.di

import friends.mobile.feature.profile.data.mapper.ProfileMapper
import friends.mobile.feature.profile.data.remote.ProfileApi
import friends.mobile.feature.profile.data.repository.ProfileRepositoryImpl
import friends.mobile.feature.profile.data.usecase.GetMeUseCaseImpl
import friends.mobile.feature.profile.data.usecase.UpdateProfileUseCaseImpl
import friends.mobile.feature.profile.domain.repository.ProfileRepository
import friends.mobile.feature.profile.domain.usecase.GetMeUseCase
import friends.mobile.feature.profile.domain.usecase.UpdateProfileUseCase
import friends.mobile.feature.profile.presentation.edit.EditProfileViewModel
import friends.mobile.feature.profile.presentation.profile.ProfileViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val profileModule = module {
    single<ProfileApi> {
        ProfileApi(get(named("auth")))
    }
    single { ProfileMapper() }
    single<ProfileRepository> {
        ProfileRepositoryImpl(
            api = get(),
            mapper = get()
        )
    }
    single<GetMeUseCase> { GetMeUseCaseImpl(get()) }
    single<UpdateProfileUseCase> { UpdateProfileUseCaseImpl(get()) }

    factory { ProfileViewModel(get()) }
    factory { EditProfileViewModel(get()) }
}
