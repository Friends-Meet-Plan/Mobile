//
//  RootView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct RootView: View {
    @State private var router = Router()
    
    var body: some View {
        NavigationStack(path: $router.path) {
            currentView()
                .navigationDestination(for: AppRouter.self) { route in
                    switch route {
                    case let .editProfile(profile):
                        EditProfileView(profile: profile)
                    case let .createEvent(date):
                        CreateEventView(date: date)
                    case let .eventDetail(id):
                        EventDetailView(eventId: id)
                    default:
                        EmptyView()
                    }
                }
        }
        .environment(router)
    }
    
    @ViewBuilder
    private func currentView() -> some View {
        if router.session != nil {
            TabBarView()
        } else {
            LoginView { newSession in
                router.session = newSession
            }
        }
    }
}
