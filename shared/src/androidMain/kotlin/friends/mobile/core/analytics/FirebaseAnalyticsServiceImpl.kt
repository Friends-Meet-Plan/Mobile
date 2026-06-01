package friends.mobile.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

internal class FirebaseAnalyticsServiceImpl : AnalyticsService {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    override fun logScreenView(screenName: String) {
        analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            },
        )
        analytics.logEvent(screenName, null)
    }

    override fun logEvent(name: String, params: Map<String, String>) {
        val bundle = Bundle()
        params.forEach { (key, value) -> bundle.putString(key, value) }
        analytics.logEvent(name, bundle)
    }

    override fun logError(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }
}
