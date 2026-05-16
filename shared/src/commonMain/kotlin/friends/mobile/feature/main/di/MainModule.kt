package friends.mobile.feature.main.di

import friends.mobile.feature.main.data.mapper.MainEventMapper
import friends.mobile.feature.main.data.repository.MainRepositoryImpl
import friends.mobile.feature.main.domain.repository.MainRepository
import friends.mobile.feature.main.presentation.MainViewModel
import org.koin.dsl.module

val mainModule = module {
    single<MainEventMapper> {
        MainEventMapper()
    }

    single<MainRepository> {
        MainRepositoryImpl(
            api = get(),
            eventMapper = get(),
        )
    }

    factory {
        MainViewModel()
    }
}
