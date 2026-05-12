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
        VStack(spacing: 16) {
            TextField("Username", text: Binding(
                get: { reducer.username },
                set: { reducer.updateUsername($0) }
            ))
            .textFieldStyle(.roundedBorder)
            
            TextField("Bio", text: Binding(
                get: { reducer.bio },
                set: { reducer.updateBio($0) }
            ))
            .textFieldStyle(.roundedBorder)
            .lineLimit(3...5)
            
            TextField("Avatar URL", text: Binding(
                get: { reducer.avatarUrl },
                set: { reducer.updateAvatarUrl($0) }
            ))
            .textFieldStyle(.roundedBorder)
            
            if let error = reducer.saveError {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
            }
            
            Spacer()
            
            Button {
                reducer.save()
            } label: {
                if reducer.isSaving {
                    ProgressView()
                        .progressViewStyle(.circular)
                } else {
                    Text("Save")
                }
            }
            .tint(reducer.isSaving ? .gray : .green)
        }
        .navigationTitle("Edit Profile")
        .navigationBarTitleDisplayMode(.inline)
        .task {
            reducer.onNavigateBack = {
                router.pop()
            }
        }
    }
}
