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
    
    var body: some View {
        VStack(spacing: 0) {
            SearchBar(
                text: $reducer.searchText,
                onSearch: { query in
                    reducer.onSearchUsers(query)
                }, onClear: {
                    reducer.clearSearch()
                }
            )
            .padding()
            .background(Color(.systemBackground))
            
            if let errorMessage = reducer.errorMessage {
                ErrorBanner(message: errorMessage)
            }
            
            contentListView()
                .overlay {
                    if reducer.isLoading {
                        ScrollView {
                            LazyVStack(spacing: 12) {
                                ForEach(0..<8, id: \.self) { _ in
                                    UserRowSkeleton()
                                }
                            }
                            .padding()
                        }
                    }
                }
                .opacity(reducer.isLoading ? 0 : 1)
                .safeAreaInset(edge: .bottom) {
                    VStack(spacing: 0) {
                        
                        Picker("", selection: $selectedSegment) {
                            ForEach(Segment.allCases, id: \.self) { segment in
                                Text(segment.rawValue)
                                    .tag(segment)
                            }
                        }
                        .pickerStyle(.segmented)
                        .padding()
                        .background(Color(.systemBackground))
                        .onChange(of: selectedSegment) { oldValue, newValue in
                            if reducer.searchResults != nil {
                                reducer.clearSearch()
                            }
                            reducer.onTabSelected(newValue.requestTab)
                        }
                    }
                }
        }
    }
    
    @ViewBuilder
    private func contentListView() -> some View {
        let listToDisplay = displayedList()
        let isEmpty = listToDisplay.isEmpty
        let isSearching = reducer.isSearching
        
        if isSearching {
            VStack(spacing: 12) {
                ProgressView()
                Text("Searching...")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(.systemBackground))
        } else if isEmpty {
            emptyStateView()
        } else {
            List(listToDisplay, id: \.id) { user in
                UserRowView(user: user)
                    .listRowSeparator(.hidden)
            }
            .listStyle(.plain)
        }
    }
    
    private func displayedList() -> [Shared.UserDto] {
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
        VStack(spacing: 16) {
            Image(systemName: "person.2")
                .font(.system(size: 48))
                .foregroundColor(.secondary)
            
            if let searchResults = reducer.searchResults, searchResults.isEmpty {
                VStack(spacing: 8) {
                    Text("No users found")
                        .font(.headline)
                    Text("Try searching with a different name")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
            } else {
                VStack(spacing: 8) {
                    Text(emptyStateTitle())
                        .font(.headline)
                    Text(emptyStateSubtitle())
                        .font(.subheadline)
                        .foregroundColor(.secondary)
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
    let user: Shared.UserDto
    
    var body: some View {
        HStack(spacing: 14) {
            
            Circle()
                .fill(.gray.opacity(0.15))
                .frame(width: 42, height: 42)
                .overlay {
                    Text(user.username.prefix(1).uppercased())
                        .font(.subheadline.weight(.semibold))
                }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(user.username)
                    .font(.subheadline.weight(.medium))
                
                if let bio = user.bio, !bio.isEmpty {
                    Text(bio)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                        .lineLimit(1)
                }
            }
            
            Spacer()
        }
        .padding(14)
        .background {
            RoundedRectangle(cornerRadius: 16)
                .fill(.gray.opacity(0.06))
        }
    }
}

private struct UserRowSkeleton: View {
    var body: some View {
        HStack(spacing: 14) {
            
            Circle()
                .fill(.gray.opacity(0.2))
                .frame(width: 42, height: 42)
            
            VStack(alignment: .leading, spacing: 8) {
                
                RoundedRectangle(cornerRadius: 4)
                    .fill(.gray.opacity(0.2))
                    .frame(width: 120, height: 12)
                
                RoundedRectangle(cornerRadius: 4)
                    .fill(.gray.opacity(0.15))
                    .frame(width: 180, height: 10)
            }
            
            Spacer()
        }
        .padding(14)
        .background {
            RoundedRectangle(cornerRadius: 16)
                .fill(.gray.opacity(0.06))
        }
    }
}
