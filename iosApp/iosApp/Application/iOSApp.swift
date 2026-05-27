import SwiftUI
import Shared
import FirebaseCore
import FirebaseCrashlytics

@main
struct iOSApp: App {

    init() {
        FirebaseApp.configure()
        IOSKoinInitializerKt.doInitKoinIOS(
            analyticsService: FirebaseAnalyticsServiceImpl()
        )
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
