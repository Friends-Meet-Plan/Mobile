//
//  CreateEventReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 13.05.2026.
//

import SwiftUI
import Shared

@Observable
final class CreateEventReducer {
    
    let selectedDate: String
    var title: String = ""
    var descriptionText: String = ""
    var location: String = ""
    
    var availableFriends: [User] = []
    var selectedFriendIds: Set<String> = []
    var isLoadingFriends: Bool = false
    var friendsError: String?
    var showFriendsSheet: Bool = false
    
    var isCreatingEvent: Bool = false
    var isCreateButtonEnabled: Bool = false
    var errorMessage: String?
    
    var onNavigateToEventDetail: ((String) -> Void)?
    var onNavigateBack: (() -> Void)?
    
    // MARK: - Private Properties
    private let sharedVM: CreateEventViewModel
    private var stateTask: Task<Void, Never>?
    private var actionTask: Task<Void, Never>?
    
    // MARK: - Initialization
    init(dateString: String) {
        self.selectedDate = dateString
        self.sharedVM = CreateEventViewModel(selectedDate: dateString)
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let createEventState = state as? CreateEventViewState else { continue }
                switch createEventState {
                case is CreateEventViewState.Loading:
                    self.isLoadingFriends = true
                    self.errorMessage = nil
                case let error as CreateEventViewState.Error:
                    self.errorMessage = error.message
                    self.isLoadingFriends = false
                case let content as CreateEventViewState.Content:
                    self.title = content.title
                    self.descriptionText = content.description_
                    self.location = content.location
                    self.availableFriends = content.availableFriends
                    self.selectedFriendIds = content.selectedFriendIds
                    self.isLoadingFriends = content.isLoadingFriends
                    self.friendsError = content.friendsError
                    self.isCreatingEvent = content.isCreatingEvent
                    self.isCreateButtonEnabled = content.isCreateButtonEnabled
                    self.errorMessage = nil
                default:
                    self.errorMessage = nil
                    self.isLoadingFriends = false
                }
            }
        }
        
        actionTask = Task {
            let stream = sharedVM.viewActions.asAsyncStream(scope: scope)
            for await rawAction in stream {
                guard let action = rawAction as? CreateEventAction else { continue }
                
                switch action {
                case is CreateEventAction.NavigateBack:
                    self.onNavigateBack?()
                case let nav as CreateEventAction.NavigateToEventDetail:
                    self.onNavigateToEventDetail?(nav.eventId)
                default:
                    break
                }
            }
        }
    }
    
    deinit {
        stateTask?.cancel()
        actionTask?.cancel()
        sharedVM.clear()
    }
    
    func updateTitle(_ value: String) {
        sharedVM.obtainEvent(event: CreateEventEvent.OnTitleChanged(title: value))
    }
    
    func updateDescription(_ value: String) {
        sharedVM.obtainEvent(event: CreateEventEvent.OnDescriptionChanged(description: value))
    }
    
    func updateLocation(_ value: String) {
        sharedVM.obtainEvent(event: CreateEventEvent.OnLocationChanged(location: value))
    }
    
    func toggleFriend(_ friendId: String) {
        sharedVM.obtainEvent(event: CreateEventEvent.OnToggleFriend(friendId: friendId))
    }
    
    func toggleFriendsSheet() {
        sharedVM.obtainEvent(event: CreateEventEvent.OnSelectFriendsSheet())
    }
    
    func submit() {
        sharedVM.obtainEvent(event: CreateEventEvent.OnCreateEvent())
    }
    
    func back() {
        sharedVM.obtainEvent(event: CreateEventEvent.OnBack())
    }
}
