package friends.mobile.feature.wishplaces.di

import friends.mobile.core.di.QualifierAuthClient
import friends.mobile.feature.wishplaces.data.mapper.WishPlaceMapper
import friends.mobile.feature.wishplaces.data.remote.WishPlacesApi
import friends.mobile.feature.wishplaces.data.repository.WishPlacesRepositoryImpl
import friends.mobile.feature.wishplaces.data.usecase.ArchiveWishPlaceUseCaseImpl
import friends.mobile.feature.wishplaces.data.usecase.CreateWishPlaceUseCaseImpl
import friends.mobile.feature.wishplaces.data.usecase.GetWishPlacesUseCaseImpl
import friends.mobile.feature.wishplaces.domain.repository.WishPlacesRepository
import friends.mobile.feature.wishplaces.domain.usecase.ArchiveWishPlaceUseCase
import friends.mobile.feature.wishplaces.domain.usecase.CreateWishPlaceUseCase
import friends.mobile.feature.wishplaces.domain.usecase.GetWishPlacesUseCase
import friends.mobile.feature.wishplaces.presentation.WishPlacesViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val wishPlacesModule = module {
    single { WishPlacesApi(get(named<QualifierAuthClient>())) }
    single { WishPlaceMapper() }
    single<WishPlacesRepository> {
        WishPlacesRepositoryImpl(
            api = get(),
            mapper = get()
        )
    }

    factory<GetWishPlacesUseCase> { GetWishPlacesUseCaseImpl(get()) }
    factory<CreateWishPlaceUseCase> { CreateWishPlaceUseCaseImpl(get()) }
    factory<ArchiveWishPlaceUseCase> { ArchiveWishPlaceUseCaseImpl(get()) }

    factory { WishPlacesViewModel() }
}
