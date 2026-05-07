package friends.mobile.feature.friends.domain.model

data class PaginationInfo(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)