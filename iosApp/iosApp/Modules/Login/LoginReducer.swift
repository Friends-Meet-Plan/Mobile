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
    var errorMessage: String?
    
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
                if let navigateHome = action as? LoginAction.NavigateToHome {
                    onLoginSuccess?(navigateHome.session)
                } else if let showMessage = action as? LoginAction.ShowMessage {
                    self.errorMessage = showMessage.message
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
        sharedVM.obtainEvent(event: LoginEvent.OnUsernameChanged(value: username))
    }

    func setPassword(_ password: String) {
        self.password = password
        sharedVM.obtainEvent(event: LoginEvent.OnPasswordChanged(value: password))
    }
    
    func login() {
        sharedVM.obtainEvent(event: LoginEvent.OnLoginClick())
    }
}
