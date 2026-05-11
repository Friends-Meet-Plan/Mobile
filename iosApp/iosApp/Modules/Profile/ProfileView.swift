//
//  ProfileView.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

struct ProfileView: View {
    var onLogout: (() -> Void)?
    
    @State private var profileReducer = ProfileReducer()
    
    var body: some View {
        if let profile = profileReducer.profile {
            VStack(spacing: 24) {
                UserView(user: profile, dimension: .vertical)
                    .frame(maxWidth: .infinity)
                
                Spacer()
                
                Button("Log Out") {
                    profileReducer.logout()
                }
                .tint(.red)
            }
            .padding()
            .navigationTitle("Profile")
            .task {
                profileReducer.onLogoutRequested = onLogout
            }
        } else {
            LoadingView()
                .task {
                    profileReducer.loadProfile()
                }
        }
    }
}
