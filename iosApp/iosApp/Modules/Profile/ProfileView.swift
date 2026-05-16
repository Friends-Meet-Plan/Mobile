//
//  ProfileView.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

struct ProfileView: View {
    
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
                    profileReducer.navigateToEdit()
                }
                .tint(.blue)
                
                Button("Log Out") {
                    profileReducer.logout()
                }
                .tint(.red)
            }
            .padding()
            .navigationTitle("Profile")
            .onAppear {
                // TODO: костыль обновления
                profileReducer.loadProfile()
            }
            .task {
                profileReducer.onLogoutRequested = {
                    router.login()
                }
                profileReducer.onEditProfileRequested = {
                    router.push(screen: .editProfile(profile: profile))
                }
            }
        } else {
            LoadingView()
                .task {
                    profileReducer.loadProfile()
                }
        }
    }
}
