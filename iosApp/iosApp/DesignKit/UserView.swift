//
//  UserView.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

enum Dimension {
    case horizontal, vertical
}

struct UserView: View {
    let user: IUser
    let dimension: Dimension
    
    var body: some View {
        container(dimension: dimension) {
            Circle()
                .fill(.gray.opacity(0.2))
                .frame(width: 80, height: 80)
                .overlay {
                    Text(user.username.prefix(1).uppercased())
                        .font(.title.weight(.semibold))
                }
            
            VStack(spacing: 8) {
                Text(user.username)
                    .font(.headline)
                
                if let bio = user.bio, !bio.isEmpty {
                    Text(bio)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
            }
        }
    }
    
    @ViewBuilder
    private func container<Content: View>(
        dimension: Dimension,
        @ViewBuilder content: () -> Content
    ) -> some View {
        switch dimension {
        case .horizontal:
            HStack {
                content()
            }

        case .vertical:
            VStack {
                content()
                Spacer()
            }
        }
    }
}
