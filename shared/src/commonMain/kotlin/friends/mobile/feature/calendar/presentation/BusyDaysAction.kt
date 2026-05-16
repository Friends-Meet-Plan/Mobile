package friends.mobile.feature.calendar.presentation

sealed class BusyDaysAction {

    data object RefreshCompleted : BusyDaysAction()

    data class ShowError(
        val message: String,
    ) : BusyDaysAction()
}
