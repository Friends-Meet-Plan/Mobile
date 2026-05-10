//
//  FriendProfileView.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

struct FriendProfileView: View {
    
    let userId: String
    let onDismiss: () -> Void
    @State private var reducer: FriendProfileReducer?
    
    var body: some View {
        VStack(spacing: 0) {
            if let reducer {
                if let user = reducer.user {
                    VStack(spacing: 24) {
                        VStack(spacing: 16) {
                            UserCircle(name: user.username)
                            
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
                        .frame(maxWidth: .infinity)
                        
                        if let actionError = reducer.actionError {
                            ErrorBanner(message: actionError)
                        }
                        
                        actionButtons(reducer: reducer)
                        
                        Spacer()
                    }
                    .padding()
                    .opacity(reducer.isActionPending ? 0.6 : 1)
                    .disabled(reducer.isActionPending)
                } else if reducer.isLoading {
                    LoadingView()
                } else {
                    ErrorBanner(message: "Failed to load profile")
                }
            } else {
                LoadingView()
            }
        }
        .navigationTitle("Profile")
        .navigationBarTitleDisplayMode(.inline)
        .task {
            let newReducer = FriendProfileReducer(userId: userId)
            newReducer.onRefreshFriendsRequested = {
                onDismiss()
            }
            self.reducer = newReducer
        }
    }
    
    @ViewBuilder
    private func actionButtons(reducer: FriendProfileReducer) -> some View {
        let isLoading = reducer.isActionPending
        
        switch reducer.status {
        case .none:
            Button(action: { reducer.sendFriendRequest() }) {
                Text("Add Friend")
                    .frame(maxWidth: .infinity)
            }
            .tint(.blue)
            .disabled(isLoading)
        case .requesting:
            Button(action: {}) {
                Text("Request Sent")
                    .frame(maxWidth: .infinity)
            }
            .tint(.gray)
            .disabled(true)
        case .incoming:
            HStack(spacing: 12) {
                Button(action: { reducer.acceptRequest() }) {
                    Text("Accept")
                        .frame(maxWidth: .infinity)
                }
                .tint(.green)
                .disabled(isLoading)
                
                Button(action: { reducer.rejectRequest() }) {
                    Text("Decline")
                        .frame(maxWidth: .infinity)
                }
                .tint(.red)
                .disabled(isLoading)
            }
        case .friends:
            Button(action: { reducer.removeFriend() }) {
                Text("Remove Friend")
                    .frame(maxWidth: .infinity)
            }
            .tint(.red)
            .disabled(isLoading)
        default:
            EmptyView()
        }
    }
}

#Preview {
    NavigationStack {
        FriendProfileView(userId: "123", onDismiss: {})
    }
}
