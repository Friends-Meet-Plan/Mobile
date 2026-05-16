package friends.mobile.feature.auth.di

import friends.mobile.feature.auth.data.mapper.AuthSessionMapper
import friends.mobile.feature.auth.data.mapper.StoredSessionMapper
import friends.mobile.feature.auth.data.remote.AuthApi
import friends.mobile.feature.auth.data.remote.AuthenticatedClient
import friends.mobile.feature.auth.data.repository.AuthRepositoryImpl
import friends.mobile.feature.auth.data.storage.TokenStorage
import friends.mobile.feature.auth.data.storage.TokenStorageImpl
import friends.mobile.feature.auth.data.usecase.GetStoredSessionUseCaseImpl
import friends.mobile.feature.auth.data.usecase.LoginUseCaseImpl
import friends.mobile.feature.auth.data.usecase.LogoutUseCaseImpl
import friends.mobile.feature.auth.data.usecase.RegisterUseCaseImpl
import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.feature.auth.domain.usecase.GetStoredSessionUseCase
import friends.mobile.feature.auth.domain.usecase.LoginUseCase
import friends.mobile.feature.auth.domain.usecase.LogoutUseCase
import friends.mobile.feature.auth.domain.usecase.RegisterUseCase
import friends.mobile.feature.auth.presentation.RootViewModel
import friends.mobile.feature.auth.presentation.login.LoginViewModel
import friends.mobile.feature.auth.presentation.register.RegisterViewModel
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    single<HttpClient>(named("auth")) {
        val authClient = get<HttpClient>().config { }
        AuthenticatedClient(
            client = authClient,
            storage = get(),
            onRefresh = {
                get<AuthRepository>().refresh().getOrThrow()
            },
            onUnauthorized = {
                get<AuthRepository>().logout()
            },
        )
        authClient
    }

    single<TokenStorage> {
        TokenStorageImpl(
            settings = get(),
            json = get(),
            mapper = get(),
        )
    }

    single<AuthApi> {
        AuthApi(client = get())
    }

    factory { AuthSessionMapper() }
    factory { StoredSessionMapper() }

    single<AuthRepository> {
        AuthRepositoryImpl(
            api = get(),
            storage = get(),
            mapper = get(),
        )
    }

    factory<LoginUseCase> { LoginUseCaseImpl(repository = get()) }
    factory<RegisterUseCase> { RegisterUseCaseImpl(repository = get()) }
    factory<LogoutUseCase> { LogoutUseCaseImpl(repository = get()) }
    factory<GetStoredSessionUseCase> { GetStoredSessionUseCaseImpl(repository = get()) }

    factory { RootViewModel(get(), get()) }
    factory { LoginViewModel() }
    factory { RegisterViewModel() }
}
