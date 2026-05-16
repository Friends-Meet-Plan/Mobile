package friends.mobile.feature.calendar.presentation

import friends.mobile.feature.calendar.domain.model.CalendarResponse

sealed class BusyDaysViewState {

    data object Loading : BusyDaysViewState()

    data class Content(
        val calendarResponse: CalendarResponse,
        val isRefreshing: Boolean = false,
    ) : BusyDaysViewState()

    data class Error(
        val message: String,
    ) : BusyDaysViewState()
}
