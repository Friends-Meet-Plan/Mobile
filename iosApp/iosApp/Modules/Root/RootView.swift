//
//  RootView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct RootView: View {
    @State var session: AuthSession?
    
    var body: some View {
        if let session {
            MainView(user: session.user)
        } else {
            LoginView { session in
                self.session = session
            }
        }
    }
}

#Preview {
    RootView()
}
