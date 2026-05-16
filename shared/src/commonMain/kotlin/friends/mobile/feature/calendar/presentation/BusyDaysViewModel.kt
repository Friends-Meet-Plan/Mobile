package friends.mobile.feature.calendar.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.calendar.domain.usecase.GetBusyDaysUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BusyDaysViewModel(
    private val userId: String,
) : BaseViewModel<BusyDaysViewState, BusyDaysAction, BusyDaysEvent>(
    initState = BusyDaysViewState.Loading,
),
    KoinComponent {

    private val getBusyDaysUseCase: GetBusyDaysUseCase by inject()

    init {
        obtainEvent(BusyDaysEvent.OnLoadBusyDays)
    }

    override fun obtainEvent(event: BusyDaysEvent) {
        when (event) {
            is BusyDaysEvent.OnLoadBusyDays -> loadBusyDays(showLoading = true)
            is BusyDaysEvent.OnRefresh -> loadBusyDays(showLoading = false)
            is BusyDaysEvent.OnRetry -> onRetry()
        }
    }

    private fun loadBusyDays(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
                viewState = BusyDaysViewState.Loading
            }

            when (val result = getBusyDaysUseCase(userId)) {
                is ResultWrapper.Success -> {
                    viewState = BusyDaysViewState.Content(
                        calendarResponse = result.data,
                        isRefreshing = false,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    val errorMessage = getErrorMessage(userError)
                    viewState = BusyDaysViewState.Error(
                        message = errorMessage,
                    )
                }
            }
        }
    }

    private fun onRetry() {
        viewState = BusyDaysViewState.Loading
        viewModelScope.launch {
            when (val result = getBusyDaysUseCase(userId)) {
                is ResultWrapper.Success -> {
                    viewState = BusyDaysViewState.Content(
                        calendarResponse = result.data,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = BusyDaysViewState.Error(
                        message = getErrorMessage(userError),
                    )
                }
            }
        }
    }
}
