package friends.mobile.feature.calendar.presentation

sealed class BusyDaysEvent {

    data object OnLoadBusyDays : BusyDaysEvent()

    data object OnRefresh : BusyDaysEvent()

    data object OnRetry : BusyDaysEvent()
}
