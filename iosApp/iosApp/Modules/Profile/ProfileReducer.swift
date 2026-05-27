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
    var isRefreshing = false
    var errorMessage: String?

    var onLogoutRequested: (() -> Void)?
    var onEditProfileRequested: (() -> Void)?
    
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
                    self.isRefreshing = false
                case let error as ProfileViewState.Error:
                    self.errorMessage = error.message
                    self.isRefreshing = false
                case let content as ProfileViewState.Content:
                    self.profile = content.profile
                    self.isRefreshing = content.isRefreshing
                default:
                    isLoading = false
                    isRefreshing = false
                    profile = nil
                }
            }
        }
        
        actionTask = Task {
            let stream = sharedVM.viewActions.asAsyncStream(scope: scope)
            for await rawAction in stream {
                guard let action = rawAction as? ProfileAction else { continue }
                switch action {
                case is ProfileAction.NavigateToLogin:
                    self.onLogoutRequested?()
                case is ProfileAction.NavigateToEdit:
                    self.onEditProfileRequested?()
                case let message as ProfileAction.ShowMessage:
                    self.errorMessage = message.message
                default: break
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
    
    func navigateToEdit() {
        sharedVM.obtainEvent(event: ProfileEvent.OnEditClick())
    }

    func onRefreshProfile() {
        sharedVM.obtainEvent(event: ProfileEvent.OnRefreshProfile())
    }
}
