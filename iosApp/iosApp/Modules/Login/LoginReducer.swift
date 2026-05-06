//
//  LoginReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

@Observable
final class LoginReducer {
    
    var username = ""
    var password = ""
    var isLoading = false
    var errorMessage: String? = nil
    
    var onLoginSuccess: ((AuthSession) -> Void)?
    
    private let sharedVM = LoginViewModel()
    private var stateTask: Task<Void, Never>?
    private var actionTask: Task<Void, Never>?
    
    init() {
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                switch state {
                case is LoginViewState.Loading:
                    isLoading = true
                    errorMessage = nil
                case let error as LoginViewState.Error:
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
                guard let action = rawAction as? LoginAction else { continue }
                if let success = action as? LoginAction.LoginSucceeded {
                    onLoginSuccess?(success.session)
                }
            }
        }
    }
    
    deinit {
        stateTask?.cancel()
        actionTask?.cancel()
        sharedVM.clear()
    }
    
    func login() {
        sharedVM.obtainEvent(
            event: LoginEvent.OnLoginClick(
                username: username,
                password: password
            )
        )
    }
}
