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
            ScrollView {
                VStack(spacing: 24) {
                    UserView(user: profile, dimension: .vertical)
                        .frame(maxWidth: .infinity)
                    BusyDayView(userId: profile.id)
                        .frame(maxWidth: .infinity, alignment: .topLeading)
                    WishPlacesView(userId: profile.id, mode: .editable)
                        .frame(maxWidth: .infinity, alignment: .topLeading)
                    VStack(spacing: 12) {
                        Button("Edit Profile") {
                            profileReducer.navigateToEdit()
                        }
                        .tint(.blue)
                        .frame(maxWidth: .infinity)
                        
                        Button("Log Out") {
                            profileReducer.logout()
                        }
                        .tint(.red)
                        .frame(maxWidth: .infinity)
                    }
                }
                .padding()
            }
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
