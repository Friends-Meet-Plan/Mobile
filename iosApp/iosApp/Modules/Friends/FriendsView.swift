//
//  FriendsView.swift
//  iosApp
//
//  Created by Данил Забинский on 07.05.2026.
//

import Shared
import SwiftUI

struct FriendsView: View {

    @State private var reducer = FriendsReducer()
    @State private var selectedSegment: Segment = .friends
    @State private var friendToPresent: Shared.User?

    var body: some View {
        VStack(spacing: 0) {
            // Search Bar
            SearchBar(
                text: $reducer.searchText,
                onSearch: { query in
                    reducer.onSearchUsers(query)
                }, onClear: {
                    reducer.clearSearch()
                }
            )
            .padding(DesignTheme.Spacing.lg)
            .background(Color(.systemBackground))

            // Error Banner
            if let errorMessage = reducer.errorMessage {
                ErrorBanner(message: errorMessage)
                    .padding(.horizontal, DesignTheme.Spacing.lg)
                    .padding(.vertical, DesignTheme.Spacing.md)
            }

            // Content
            contentListView()
                .overlay {
                    if reducer.isLoading {
                        LoadingView()
                    }
                }
                .opacity(reducer.isLoading ? 0 : 1)
                .safeAreaInset(edge: .bottom) {
                    // Tab Picker at Bottom
                    Picker("", selection: $selectedSegment) {
                        ForEach(Segment.allCases, id: \.self) { segment in
                            Text(segment.rawValue)
                                .tag(segment)
                        }
                    }
                    .pickerStyle(.segmented)
                    .padding(DesignTheme.Spacing.lg)
                    .background(Color(.systemBackground))
                    .onChange(of: selectedSegment) { oldValue, newValue in
                        if reducer.searchResults != nil {
                            reducer.clearSearch()
                        }
                        reducer.onTabSelected(newValue.requestTab)
                    }
                }
        }
        .task {
            reducer.reloadCurrentTab()
        }
        .sheet(item: $friendToPresent) { friend in
            FriendProfileView(reducer: FriendProfileReducer(userId: friend.id))
                .onDisappear {
                    reducer.reloadCurrentTab()
                }
        }
    }

    @ViewBuilder
    private func contentListView() -> some View {
        let listToDisplay = displayedList()
        let isEmpty = listToDisplay.isEmpty
        let isSearching = reducer.isSearching

        if isSearching {
            VStack(spacing: DesignTheme.Spacing.lg) {
                ProgressView()
                    .scaleEffect(1.2)
                Text("Searching...")
                    .font(DesignTheme.Typography.body)
                    .foregroundColor(.secondary)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(.systemBackground))
        } else if isEmpty {
            emptyStateView()
        } else {
            List(listToDisplay, id: \.id) { user in
                UserRowView(
                    user: user,
                    currentTab: reducer.currentTab,
                    searchText: reducer.searchText
                )
                .listRowSeparator(.hidden)
                .listRowInsets(EdgeInsets(top: DesignTheme.Spacing.xs, leading: DesignTheme.Spacing.lg, bottom: DesignTheme.Spacing.xs, trailing: DesignTheme.Spacing.lg))
                .onTapGesture {
                    friendToPresent = user
                }
            }
            .listStyle(.plain)
            .background(Color(.systemBackground))
        }
    }

    private func displayedList() -> [Shared.User] {
        if let searchResults = reducer.searchResults {
            return searchResults
        }

        switch reducer.currentTab {
        case .friends:
            return reducer.friendsList
        case .incoming:
            return reducer.incomingRequests
        case .outgoing:
            return reducer.outgoingRequests
        default:
            fatalError("not implemeted")
        }
    }

    @ViewBuilder
    private func emptyStateView() -> some View {
        VStack(spacing: DesignTheme.Spacing.lg) {
            Image(systemName: "person.2")
                .font(.system(size: 56))
                .foregroundColor(DesignTheme.accentColor.opacity(0.3))

            if let searchResults = reducer.searchResults, searchResults.isEmpty {
                VStack(spacing: DesignTheme.Spacing.sm) {
                    Text("No users found")
                        .font(DesignTheme.Typography.captionSemibold)
                        .foregroundColor(.black)
                    Text("Try searching with a different name")
                        .font(DesignTheme.Typography.bodySmall)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
            } else {
                VStack(spacing: DesignTheme.Spacing.sm) {
                    Text(emptyStateTitle())
                        .font(DesignTheme.Typography.captionSemibold)
                        .foregroundColor(.black)
                    Text(emptyStateSubtitle())
                        .font(DesignTheme.Typography.bodySmall)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }

    private func emptyStateTitle() -> String {
        switch reducer.currentTab {
        case .friends:
            return "No friends yet"
        case .incoming:
            return "No incoming requests"
        case .outgoing:
            return "No outgoing requests"
        default:
            fatalError("not implemeted")
        }
    }

    private func emptyStateSubtitle() -> String {
        switch reducer.currentTab {
        case .friends:
            return "Search and send friend requests to get started"
        case .incoming:
            return "You will see incoming requests here"
        case .outgoing:
            return "Requests you have sent will appear here"
        default:
            fatalError("not implemeted")
        }
    }
}

private struct UserRowView: View {
    let user: Shared.User
    let currentTab: RequestTab
    let searchText: String

    var body: some View {
        HStack(spacing: DesignTheme.Spacing.md) {
            // Avatar with Initial Badge
            ZStack(alignment: .bottomTrailing) {
                Circle()
                    .fill(DesignTheme.accentColor)
                    .frame(width: 52, height: 52)
                    .overlay {
                        Text(user.username.prefix(1).uppercased())
                            .font(DesignTheme.Typography.captionSemibold)
                            .foregroundColor(.white)
                    }

                // Status Badge
                if let avatarUrl = user.avatarUrl, !avatarUrl.isEmpty {
                    AsyncImage(url: URL(string: avatarUrl)) { image in
                        image
                            .resizable()
                            .scaledToFill()
                    } placeholder: {
                        Circle()
                            .fill(DesignTheme.accentColor)
                    }
                    .frame(width: 52, height: 52)
                    .clipShape(Circle())
                }
            }

            // User Info
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                Text(user.username)
                    .font(DesignTheme.Typography.captionSemibold)
                    .foregroundColor(.black)

                if let bio = user.bio, !bio.isEmpty {
                    Text(bio)
                        .font(DesignTheme.Typography.bodySmallest)
                        .foregroundColor(.secondary)
                        .lineLimit(1)
                }
            }

            Spacer()

            // Status Badge
            if searchText.isEmpty {
                statusBadge()
            }
        }
        .padding(DesignTheme.Spacing.md)
        .background(
            RoundedRectangle(cornerRadius: DesignTheme.CornerRadius.medium)
                .fill(Color(.systemGray6).opacity(0.5))
        )
    }

    @ViewBuilder
    private func statusBadge() -> some View {
        switch currentTab {
        case .friends:
            Image(systemName: "checkmark.circle.fill")
                .font(.system(size: 16))
                .foregroundColor(DesignTheme.secondaryAccent)

        case .incoming:
            VStack(spacing: DesignTheme.Spacing.xs) {
                Text("Pending")
                    .font(DesignTheme.Typography.bodySmallest)
                    .foregroundColor(.white)
            }
            .padding(.horizontal, DesignTheme.Spacing.sm)
            .padding(.vertical, DesignTheme.Spacing.xs)
            .background(DesignTheme.accentColor)
            .cornerRadius(DesignTheme.CornerRadius.small)

        case .outgoing:
            VStack(spacing: DesignTheme.Spacing.xs) {
                Text("Sent")
                    .font(DesignTheme.Typography.bodySmallest)
                    .foregroundColor(.gray)
            }
            .padding(.horizontal, DesignTheme.Spacing.sm)
            .padding(.vertical, DesignTheme.Spacing.xs)
            .background(Color(.systemGray6))
            .cornerRadius(DesignTheme.CornerRadius.small)

        default:
            EmptyView()
        }
    }
}
