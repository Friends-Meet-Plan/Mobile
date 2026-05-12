//
//  FriendProfileReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

@Observable
final class FriendProfileReducer {
    
    var user: Shared.User?
    var status: Shared.FriendshipStatus = .none
    var isLoading = false
    var isActionPending = false
    var actionError: String?
    
    private let sharedVM = FriendProfileViewModel()
    private var stateTask: Task<Void, Never>?
    
    init(userId: String) {
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let profileState = state as? FriendProfileViewState else { continue }
                
                if profileState is FriendProfileViewState.Loading {
                    self.isLoading = true
                    self.actionError = nil
                } else if let errorState = profileState as? FriendProfileViewState.Error {
                    self.isLoading = false
                    self.actionError = errorState.message
                } else if let contentState = profileState as? FriendProfileViewState.Content {
                    self.isLoading = false
                    self.user = contentState.user
                    self.status = contentState.status
                    self.isActionPending = contentState.isActionPending
                    self.actionError = contentState.actionError
                }
            }
        }
        
        sharedVM.obtainEvent(event: FriendProfileEvent.ScreenOpened(userId: userId))
    }
    
    deinit {
        stateTask?.cancel()
        sharedVM.clear()
    }
    
    func sendFriendRequest() {
        guard let user else { return }
        sharedVM.obtainEvent(event: FriendProfileEvent.OnSendRequest(userId: user.id))
    }
    
    func acceptRequest() {
        guard let user else { return }
        sharedVM.obtainEvent(event: FriendProfileEvent.OnAcceptRequest(userId: user.id))
    }
    
    func rejectRequest() {
        guard let user else { return }
        sharedVM.obtainEvent(event: FriendProfileEvent.OnRejectRequest(userId: user.id))
    }
    
    func removeFriend() {
        guard let user else { return }
        sharedVM.obtainEvent(event: FriendProfileEvent.OnRemoveFriend(userId: user.id))
    }
}
