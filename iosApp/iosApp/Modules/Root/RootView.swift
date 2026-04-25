//
//  RootView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

struct RootView: View {
    @State var user: User?

    var body: some View {
        if let user {
            MainView()
                .environment(ApplicationState(user: user))
        } else {
            LoginView { loggedInUser in
                self.user = loggedInUser
            }
        }
    }
}

#Preview {
    RootView()
}
