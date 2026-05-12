//
//  ProfileReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

@Observable
final class ProfileReducer {
    
    var profile: Profile?
    var isLoading = false
    var errorMessage: String?
    
    var onLogoutRequested: (() -> Void)?
    
    private let sharedVM = ProfileViewModel()
    private var stateTask: Task<Void, Never>?
    private var actionTask: Task<Void, Never>?
    
    init() {
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let profileState = state as? ProfileViewState else { continue }
                switch profileState {
                case is ProfileViewState.Loading:
                    self.isLoading = true
                case let error as ProfileViewState.Error:
                    self.errorMessage = error.message
                case let content as ProfileViewState.Content:
                    self.profile = content.profile
                default:
                    isLoading = false
                    profile = nil
                }
            }
        }
        
        actionTask = Task {
            let stream = sharedVM.viewActions.asAsyncStream(scope: scope)
            for await rawAction in stream {
                guard let action = rawAction as? ProfileAction else { continue }
                if action is ProfileAction.LogoutRequested {
                    onLogoutRequested?()
                }
            }
        }
    }
    
    deinit {
        stateTask?.cancel()
        actionTask?.cancel()
        sharedVM.clear()
    }
    
    func loadProfile() {
        sharedVM.obtainEvent(event: ProfileEvent.OnLoadProfile())
    }
    
    func logout() {
        sharedVM.obtainEvent(event: ProfileEvent.OnLogoutClick())
    }
}
