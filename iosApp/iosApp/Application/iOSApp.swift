import SwiftUI
import Shared

@main
struct iOSApp: App {
    
    init() {
        IOSKoinInitializerKt.doInitKoinIOS()
    }
    
    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
