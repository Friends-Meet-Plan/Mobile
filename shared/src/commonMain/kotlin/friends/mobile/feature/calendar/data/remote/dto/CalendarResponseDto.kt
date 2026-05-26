package friends.mobile.feature.calendar.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalendarResponseDto(
    @SerialName("busy_days")
    val busyDays: List<BusyDayDto>,
)
