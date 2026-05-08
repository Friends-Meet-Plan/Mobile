//
//  FriendsReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 07.05.2026.
//

import Shared
import SwiftUI

@Observable
final class FriendsReducer {
    var currentTab: Shared.RequestTab = .friends
    var searchText = ""
    
    var friendsList: [Shared.UserDto] = []
    var incomingRequests: [Shared.UserDto] = []
    var outgoingRequests: [Shared.UserDto] = []
    var searchResults: [Shared.UserDto]?
    
    var isLoading = false
    var isSearching = false
    var isRequestPending = false
    var errorMessage: String?
    
    private let sharedVM = FriendsViewModel()
    private var stateTask: Task<Void, Never>?
    
    init() {
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                switch state {
                case is FriendsViewState.Loading:
                    self.isLoading = true
                    self.errorMessage = nil
                case let error as FriendsViewState.Error:
                    self.isLoading = false
                    self.errorMessage = error.message
                case let content as FriendsViewState.Content:
                    self.isLoading = false
                    self.currentTab = content.currentTab
                    self.friendsList = content.friendsList
                    self.incomingRequests = content.incomingRequests
                    self.outgoingRequests = content.outgoingRequests
                    self.searchResults = content.searchResults
                    self.isSearching = content.isSearching
                    self.isRequestPending = content.isRequestPending
                default:
                    isLoading = false
                    isSearching = false
                    isRequestPending = false
                    errorMessage = nil
                }
            }
        }
        
        sharedVM.obtainEvent(event: FriendsEvent.ScreenOpened())
    }
    
    deinit {
        stateTask?.cancel()
        sharedVM.clear()
    }
    
    func onTabSelected(_ tab: Shared.RequestTab) {
        sharedVM.obtainEvent(event: FriendsEvent.OnTabSelected(tab: tab))
    }
    
    func onSearchUsers(_ query: String) {
        guard !query.isEmpty else {
            searchResults = nil
            return
        }
        sharedVM.obtainEvent(event: FriendsEvent.OnSearchUsers(query: query))
    }
    
    func clearSearch() {
        searchResults = nil
        searchText = ""
    }
}
