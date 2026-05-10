package friends.mobile.feature.profile.di

import friends.mobile.feature.profile.data.mapper.ProfileMapper
import friends.mobile.feature.profile.data.remote.ProfileApi
import friends.mobile.feature.profile.data.repository.ProfileRepositoryImpl
import friends.mobile.feature.profile.data.usecase.GetMeUseCaseImpl
import friends.mobile.feature.profile.domain.repository.ProfileRepository
import friends.mobile.feature.profile.domain.usecase.GetMeUseCase
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
}
