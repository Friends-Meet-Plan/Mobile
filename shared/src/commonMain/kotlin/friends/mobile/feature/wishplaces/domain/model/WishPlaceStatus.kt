package friends.mobile.feature.wishplaces.domain.model

enum class WishPlaceStatus(val description: String) {
    ACTIVE("active"),
    VISITED("visited"),
    ARCHIVED("archived");

    companion object {
        fun fromString(status: String): WishPlaceStatus {
            return entries.find { it.description == status.lowercase() } ?: ACTIVE
        }
    }
}
