//
//  RegisterReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import Foundation
import Shared

@Observable
final class RegisterReducer {
    
    var username = ""
    var password = ""
    var isLoading = false
    var errorMessage: String? = nil
    
    var onRegisterSuccess: (() -> Void)?
    
    private let sharedVM = RegisterViewModel()
    private var stateTask: Task<Void, Never>?
    private var actionTask: Task<Void, Never>?
    
    init() {
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                switch state {
                case is RegisterViewState.Loading:
                    isLoading = true
                    errorMessage = nil
                case let error as RegisterViewState.Error:
                    isLoading = false
                    errorMessage = error.message
                default:
                    isLoading = false
                }
            }
        }
        
        actionTask = Task {
            let stream = sharedVM.viewActions.asAsyncStream(scope: scope)
            for await rawAction in stream {
                guard let action = rawAction as? RegisterAction else { continue }
                if action as? RegisterAction.RegisterSucceeded != nil {
                    onRegisterSuccess?()
                }
            }
        }
    }
    
    deinit {
        stateTask?.cancel()
        actionTask?.cancel()
        sharedVM.clear()
    }
    
    func setUsername(_ username: String) {
        self.username = username
        sharedVM.obtainEvent(event: RegisterEvent.OnUsernameChanged(value: username))
    }

    func setPassword(_ password: String) {
        self.password = password
        sharedVM.obtainEvent(event: RegisterEvent.OnPasswordChanged(value: password))
    }
    
    func register() {
        sharedVM.obtainEvent(
            event: RegisterEvent.OnRegisterClick()
        )
    }
}
