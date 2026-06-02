//
//  FriendProfileView.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

struct FriendProfileView: View {
    @State private var reducer: FriendProfileReducer
    
    init(reducer: FriendProfileReducer) {
        self.reducer = reducer
    }
    
    var body: some View {
        VStack(spacing: .zero) {
            if let user = reducer.user {
                ScrollView {
                    VStack(spacing: DesignTheme.Spacing.xxxl) {
                        profileHeader(user: user)
                        
                        if let actionError = reducer.actionError {
                            ErrorBanner(message: actionError)
                        }
                        
                        actionButtons()
                        
                        if reducer.status == .friends {
                            WishPlacesView(userId: user.id, mode: .readOnly)
                        }
                        
                        Spacer()
                    }
                    .padding(.horizontal, DesignTheme.Spacing.xl)
                    .padding(.vertical, DesignTheme.Spacing.xl)
                }
                .disabled(reducer.isActionPending)
                .opacity(reducer.isActionPending ? 0.6 : 1.0)
            } else if reducer.isLoading {
                VStack(spacing: DesignTheme.Spacing.lg) {
                    ProgressView()
                        .scaleEffect(1.5)
                    Text("Loading profile...")
                        .font(DesignTheme.Typography.body)
                        .foregroundColor(.gray)
                }
            } else {
                VStack(spacing: DesignTheme.Spacing.lg) {
                    Image(systemName: "exclamationmark.circle.fill")
                        .font(.system(size: 48))
                        .foregroundColor(DesignTheme.error)
                    Text("Failed to load profile")
                        .font(DesignTheme.Typography.captionSemibold)
                        .foregroundColor(.primary)
                    Text("Please try again")
                        .font(DesignTheme.Typography.bodySmall)
                        .foregroundColor(.gray)
                }
            }
        }
        .navigationTitle("Profile")
        .navigationBarTitleDisplayMode(.inline)
    }
    
    @ViewBuilder
    private func profileHeader(user: Shared.User) -> some View {
        VStack(spacing: DesignTheme.Spacing.lg) {
            if let avatarUrl = user.avatarUrl, !avatarUrl.isEmpty {
                AsyncImage(url: URL(string: avatarUrl)) { image in
                    image
                        .resizable()
                        .scaledToFill()
                } placeholder: {
                    Circle()
                        .fill(Color(UIColor.systemGray6))
                        .overlay(
                            Image(systemName: "person.fill")
                                .foregroundColor(.gray)
                        )
                }
                .frame(width: 100, height: 100)
                .clipShape(Circle())
            } else {
                UserView(user: user, dimension: .vertical)
            }
            
            VStack(spacing: DesignTheme.Spacing.xs) {
                Text(user.username)
                    .font(DesignTheme.Typography.heading)
                    .foregroundColor(.primary)
                
                if let bio = user.bio, !bio.isEmpty {
                    Text(bio)
                        .font(DesignTheme.Typography.bodySmall)
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                }
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, DesignTheme.Spacing.sm)
    }
    
    @ViewBuilder
    private func actionButtons() -> some View {
        let isLoading = reducer.isActionPending

        switch reducer.status {
        case .none:
            ButtonFactory.primaryLoading(
                action: { reducer.sendFriendRequest() },
                label: "Add Friend",
                isLoading: isLoading,
                isEnabled: !isLoading
            )

        case .requesting:
            ButtonFactory.disabled(
                label: "Request Sent",
                icon: "checkmark.circle.fill"
            )

        case .incoming:
            VStack(spacing: DesignTheme.Spacing.md) {
                ButtonFactory.primaryLoading(
                    action: { reducer.acceptRequest() },
                    label: "Accept",
                    isLoading: isLoading,
                    isEnabled: !isLoading
                )

                ButtonFactory.secondary(
                    action: { reducer.rejectRequest() },
                    label: "Decline",
                    isEnabled: !isLoading
                )
            }

        case .friends:
            ButtonFactory.destructive(
                action: { reducer.removeFriend() },
                label: "Remove Friend",
                isLoading: isLoading
            )

        default:
            EmptyView()
        }
    }
}
