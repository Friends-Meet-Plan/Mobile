//
//  EditProfileReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 12.05.2026.
//

import SwiftUI
import Shared

@Observable
final class EditProfileReducer {
    
    var username: String = ""
    var bio: String = ""
    var avatarUrl: String = ""
    var isSaving = false
    var saveError: String?
    var isLoading = false
    
    var onNavigateBack: (() -> Void)?
    
    private let sharedVM = EditProfileViewModel()
    private var stateTask: Task<Void, Never>?
    private var actionTask: Task<Void, Never>?
    
    init(profile: Profile) {
        let scope = sharedVM.viewModelScope
        
        sharedVM.obtainEvent(event: EditProfileEvent.Init(
            username: profile.username,
            bio: profile.bio ?? "",
            avatarUrl: profile.avatarUrl ?? ""
        ))
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let profileState = state as? EditProfileViewState else { continue }
                switch profileState {
                case is EditProfileViewState.Loading:
                    self.isLoading = true
                case let content as EditProfileViewState.Content:
                    self.username = content.username
                    self.bio = content.bio
                    self.avatarUrl = content.avatarUrl
                    self.isSaving = content.isSaving
                    self.saveError = content.saveError
                    self.isLoading = false
                case let error as EditProfileViewState.Error:
                    self.saveError = error.message
                    self.isLoading = false
                default:
                    break
                }
            }
        }
        
        actionTask = Task {
            let stream = sharedVM.viewActions.asAsyncStream(scope: scope)
            for await rawAction in stream {
                guard let action = rawAction as? EditProfileAction else { continue }
                if action is EditProfileAction.NavigateBack {
                    onNavigateBack?()
                }
            }
        }
    }
    
    deinit {
        stateTask?.cancel()
        actionTask?.cancel()
        sharedVM.clear()
    }
    
    func updateUsername(_ value: String) {
        sharedVM.obtainEvent(event: EditProfileEvent.OnUsernameChanged(value: value))
    }
    
    func updateBio(_ value: String) {
        sharedVM.obtainEvent(event: EditProfileEvent.OnBioChanged(value: value))
    }
    
    func updateAvatarUrl(_ value: String) {
        sharedVM.obtainEvent(event: EditProfileEvent.OnAvatarUrlChanged(value: value))
    }
    
    func save() {
        sharedVM.obtainEvent(event: EditProfileEvent.OnSaveClick())
    }
    
    func back() {
        sharedVM.obtainEvent(event: EditProfileEvent.OnBackClick())
    }
}
