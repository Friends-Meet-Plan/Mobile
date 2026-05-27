//
//  FirebaseAnalyticsServiceImpl.swift
//  iosApp
//

import Foundation
import Shared
import FirebaseAnalytics

final class FirebaseAnalyticsServiceImpl: AnalyticsService {

    func logScreenView(screenName: String) {
        Analytics.logEvent(AnalyticsEventScreenView, parameters: [
            AnalyticsParameterScreenName: screenName,
        ])
        Analytics.logEvent(screenName, parameters: nil)
    }

    func logEvent(name: String, params: [String: String]) {
        let converted = params as [String: Any]
        Analytics.logEvent(name, parameters: converted.isEmpty ? nil : converted)
    }
}
