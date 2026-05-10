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
                VStack(spacing: 16) {
                    Circle()
                        .fill(.gray.opacity(0.2))
                        .frame(width: 80, height: 80)
                        .overlay {
                            Text(profile.username.prefix(1).uppercased())
                                .font(.title.weight(.semibold))
                        }

                    VStack(spacing: 8) {
                        Text(profile.username)
                            .font(.headline)

                        if let bio = profile.bio, !bio.isEmpty {
                            Text(bio)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                    }
                }
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
