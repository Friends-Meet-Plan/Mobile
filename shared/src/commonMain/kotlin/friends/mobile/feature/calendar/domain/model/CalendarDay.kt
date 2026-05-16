package friends.mobile.feature.calendar.domain.model

import kotlinx.serialization.Serializable

data class CalendarDay(
    val date: String,
    val isBusy: Boolean,
)
