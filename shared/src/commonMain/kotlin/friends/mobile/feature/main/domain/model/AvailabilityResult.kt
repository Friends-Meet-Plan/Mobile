package friends.mobile.feature.main.domain.model

sealed class AvailabilityResult {
    data object Available : AvailabilityResult()
    data object Busy : AvailabilityResult()
}
