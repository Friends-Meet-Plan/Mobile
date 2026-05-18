//
//  AppRouter.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

enum AppRouter: Hashable {

    case login
    case editProfile(profile: Profile)
    case createEvent(date: String)
    case eventDetail(id: String)
    case pendingEvents
}
