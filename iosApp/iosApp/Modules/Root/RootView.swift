//
//  RootView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

struct RootView: View {
    @State private var observable = RootReducer()

    var body: some View {
        if let session = observable.session {
            MainView(username: session.user.username)
        } else {
            LoginView { session in
                observable.onLoginSuccess(session)
            }
        }
    }
}

#Preview {
    RootView()
}
