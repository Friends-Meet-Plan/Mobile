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
            BusyDaysEvent.OnLoadBusyDays -> loadBusyDays(showLoading = true)
            BusyDaysEvent.OnRefresh -> loadBusyDays(showLoading = false)
            BusyDaysEvent.OnRetry -> loadBusyDays(showLoading = true)
        }
    }

    private fun loadBusyDays(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
                viewState = BusyDaysViewState.Loading
            }

            viewState = when (val result = getBusyDaysUseCase(userId)) {
                is ResultWrapper.Success -> {
                    BusyDaysViewState.Content(
                        calendarResponse = result.data,
                        isRefreshing = false,
                    )
                }

                is ResultWrapper.Error -> {
                    BusyDaysViewState.Error(
                        message = getErrorMessage(
                            mapApiErrorToUserFriendly(result.error),
                        ),
                    )
                }
            }
        }
    }
}
