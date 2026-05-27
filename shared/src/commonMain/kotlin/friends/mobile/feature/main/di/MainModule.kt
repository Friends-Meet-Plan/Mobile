package friends.mobile.feature.main.di

import friends.mobile.feature.main.presentation.MainViewModel
import org.koin.dsl.module

val mainModule = module {

    factory {
        MainViewModel()
    }
}
