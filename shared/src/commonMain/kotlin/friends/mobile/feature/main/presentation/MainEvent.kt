package friends.mobile.feature.main.presentation

sealed class MainEvent {
    data object OnRefresh : MainEvent()
}
