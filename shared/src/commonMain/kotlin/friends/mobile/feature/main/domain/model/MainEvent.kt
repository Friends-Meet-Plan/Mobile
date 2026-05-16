package friends.mobile.feature.main.domain.model

data class MainEvent(
    val id: String,
    val title: String,
    val date: String,
    val time: String?,
    val creatorId: String,
    val participantCount: Int,
)
