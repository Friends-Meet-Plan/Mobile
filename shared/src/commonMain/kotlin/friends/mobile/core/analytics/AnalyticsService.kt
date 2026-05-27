package friends.mobile.core.analytics

interface AnalyticsService {
    fun logScreenView(screenName: String)
    fun logEvent(name: String, params: Map<String, String> = emptyMap())
}
