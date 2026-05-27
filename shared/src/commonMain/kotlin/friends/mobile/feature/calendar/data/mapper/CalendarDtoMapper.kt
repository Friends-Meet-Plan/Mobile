package friends.mobile.feature.calendar.data.mapper

import friends.mobile.feature.calendar.data.remote.dto.CalendarResponseDto
import friends.mobile.feature.calendar.domain.model.CalendarResponse

internal class CalendarDtoMapper {

    fun toDomain(dto: CalendarResponseDto): CalendarResponse =
        CalendarResponse(
            busyDays = dto.busyDays.map { it.date },
        )
}
