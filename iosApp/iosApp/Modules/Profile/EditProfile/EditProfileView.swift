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

            ButtonFactory.primaryLoading(
                action: { reducer.save() },
                label: "Save",
                isLoading: reducer.isSaving,
                isEnabled: !reducer.isSaving
            )
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
