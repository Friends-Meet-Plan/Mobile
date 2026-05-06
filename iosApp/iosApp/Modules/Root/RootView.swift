//
//  RootView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct RootView: View {
    @State private var session: AuthSession?
    @State private var router = Router()
    
    var body: some View {
        NavigationStack(path: $router.path) {
            CurrentView
                .navigationDestination(for: AppRouter.self) { route in
                    switch route {
                    case let .main(user):
                        MainView(user: user)
                    default:
                        EmptyView()
                    }
                }
        }
        .environment(router)
    }
    
    @ViewBuilder
    var CurrentView: some View {
        if let session {
            MainView(user: session.user)
        } else {
            LoginView { newSession in
                self.session = newSession
            }
        }
    }
}

#Preview {
    RootView()
}
