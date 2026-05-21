//
//  EditProfileView.swift
//  iosApp
//
//  Created by Данил Забинский on 12.05.2026.
//

import SwiftUI
import Shared

struct EditProfileView: View {
    @Environment(Router.self) private var router

    @State private var reducer: EditProfileReducer

    init(profile: Profile) {
        self.reducer = EditProfileReducer(profile: profile)
    }

    var body: some View {
        VStack(spacing: DesignTheme.Spacing.lg) {
            TextField("Username", text: Binding(
                get: { reducer.username },
                set: { reducer.updateUsername($0) }
            ))
            .formTextField()

            TextField("Bio", text: Binding(
                get: { reducer.bio },
                set: { reducer.updateBio($0) }
            ))
            .formTextField()

            TextField("Avatar URL", text: Binding(
                get: { reducer.avatarUrl },
                set: { reducer.updateAvatarUrl($0) }
            ))
            .formTextField()

            if let error = reducer.saveError {
                Text(error)
                    .font(DesignTheme.Typography.caption)
                    .foregroundColor(DesignTheme.error)
            }

            Spacer()

            Button(action: { reducer.save() }) {
                if reducer.isSaving {
                    ProgressView()
                        .progressViewStyle(.circular)
                        .tint(.white)
                } else {
                    Text("Save")
                        .font(DesignTheme.Typography.button)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 54)
            .foregroundColor(.white)
            .background(DesignTheme.accentColor)
            .cornerRadius(DesignTheme.CornerRadius.capsule)
            .disabled(reducer.isSaving)
        }
        .padding(DesignTheme.Spacing.lg)
        .navigationTitle("Edit Profile")
        .navigationBarTitleDisplayMode(.inline)
        .task {
            reducer.onNavigateBack = {
                router.pop()
            }
        }
    }
}
