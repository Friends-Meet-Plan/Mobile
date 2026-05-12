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
    
    @Environment(Router.self) private var router
    @State private var profileReducer = ProfileReducer()
    @State private var showEditProfile = false
    
    var body: some View {
        if let profile = profileReducer.profile {
            VStack(spacing: 24) {
                UserView(user: profile, dimension: .vertical)
                    .frame(maxWidth: .infinity)
                
                Spacer()
                
                Button("Edit Profile") {
                    router.push(screen: .editProfile(profile: profile))
                }
                .tint(.blue)
                
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
            .onAppear {
                // TODO: костыль обновления
                profileReducer.loadProfile()
            }
        } else {
            LoadingView()
                .task {
                    profileReducer.loadProfile()
                }
        }
    }
}
