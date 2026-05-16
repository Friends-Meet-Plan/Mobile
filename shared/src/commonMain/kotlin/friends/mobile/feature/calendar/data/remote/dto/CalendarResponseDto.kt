package friends.mobile.feature.calendar.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusyDayDto(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val date: String,
    @SerialName("event_id")
    val eventId: String,
)

@Serializable
data class CalendarResponseDto(
    @SerialName("busy_days")
    val busyDays: List<BusyDayDto>,
)
