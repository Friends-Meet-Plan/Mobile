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
    @State private var profileReducer: ProfileReducer!
    @State private var showEditProfile = false

    var body: some View {
        Group {
            if profileReducer == nil {
                LoadingView()
            } else if let profile = profileReducer.profile {
                ScrollView {
                    VStack(spacing: DesignTheme.Spacing.xxl) {
                        UserView(user: profile, dimension: .vertical)
                            .frame(maxWidth: .infinity)

                        BusyDayView(userId: profile.id)
                            .frame(maxWidth: .infinity, alignment: .topLeading)

                        WishPlacesView(userId: profile.id, mode: .editable)
                            .frame(maxWidth: .infinity, alignment: .topLeading)

                        VStack(spacing: DesignTheme.Spacing.md) {
                            ButtonFactory.primary(
                                action: { profileReducer.navigateToEdit() },
                                label: "Edit Profile"
                            )

                            ButtonFactory.destructive(
                                action: { profileReducer.logout() },
                                label: "Log Out"
                            )
                        }
                    }
                    .padding(DesignTheme.Spacing.lg)
                }
                .refreshable {
                    await withCheckedContinuation { continuation in
                        let _ = Task {
                            while profileReducer.isRefreshing {
                                try? await Task.sleep(
                                    nanoseconds: 100_000_000
                                )
                            }
                            continuation.resume()
                        }

                        profileReducer.onRefreshProfile()
                    }
                }
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
                    .onAppear {
                        profileReducer.loadProfile()
                    }
            }
        }
        .onAppear {
            if profileReducer == nil {
                profileReducer = ProfileReducer()
            }
        }
    }
}
