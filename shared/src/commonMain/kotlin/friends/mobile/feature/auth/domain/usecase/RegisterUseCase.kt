package friends.mobile.feature.auth.domain.usecase

interface RegisterUseCase {
    suspend operator fun invoke(
        username: String,
        password: String,
    )
}
