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
                VStack(spacing: DesignTheme.Spacing.xxl) {
                    UserView(user: profile, dimension: .vertical)
                        .frame(maxWidth: .infinity)

                    VStack(spacing: DesignTheme.Spacing.md) {
                        Button(action: { profileReducer.loadProfile() }) {
                            HStack(spacing: DesignTheme.Spacing.sm) {
                                Image(systemName: "arrow.clockwise")
                                Text("Refresh Activity")
                            }
                            .font(DesignTheme.Typography.button)
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(DesignTheme.accentColor)
                            .foregroundColor(.white)
                            .cornerRadius(DesignTheme.CornerRadius.capsule)
                        }

                        BusyDayView(userId: profile.id)
                            .frame(maxWidth: .infinity, alignment: .topLeading)
                    }

                    WishPlacesView(userId: profile.id, mode: .editable)
                        .frame(maxWidth: .infinity, alignment: .topLeading)

                    VStack(spacing: DesignTheme.Spacing.md) {
                        Button(action: { profileReducer.navigateToEdit() }) {
                            Text("Edit Profile")
                                .font(DesignTheme.Typography.button)
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(DesignTheme.accentColor)
                                .foregroundColor(.white)
                                .cornerRadius(DesignTheme.CornerRadius.capsule)
                        }

                        Button(action: { profileReducer.logout() }) {
                            Text("Log Out")
                                .font(DesignTheme.Typography.button)
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(DesignTheme.error)
                                .foregroundColor(.white)
                                .cornerRadius(DesignTheme.CornerRadius.capsule)
                        }
                    }
                }
                .padding(DesignTheme.Spacing.lg)
            }
            .navigationTitle("Profile")
            .onAppear {
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
