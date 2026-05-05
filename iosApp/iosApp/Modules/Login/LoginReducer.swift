//
//  LoginReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import Foundation
import Shared

@Observable
final class LoginReducer {

    var isLoading = false
    var errorMessage: String?

    private let viewModel = LoginViewModel()
    private var stateJob: Kotlinx_coroutines_coreJob?
    private var actionJob: Kotlinx_coroutines_coreJob?
    private var onSuccess: ((AuthSession) -> Void)?

    init() {
        stateJob = viewModel.viewStates.subscribe(
            onItem: { [weak self] item in
                guard let state = item as? LoginViewState else { return }
                self?.isLoading = state is LoginViewState.Loading
                self?.errorMessage = (state as? LoginViewState.Error)?.message
            },
            onComplete: {},
            onThrow: { _ in },
        )

        actionJob = viewModel.viewActions.subscribe(
            onItem: { [weak self] item in
                guard let action = item as? LoginAction else { return }
                if let success = action as? LoginAction.LoginSucceeded {
                    self?.onSuccess?(success.session)
                    self?.onSuccess = nil
                }
            },
            onComplete: {},
            onThrow: { _ in },
        )
    }

    deinit {
        stateJob?.cancel(cause: nil)
        actionJob?.cancel(cause: nil)
        viewModel.clear()
    }

    func login(
        user: User,
        onSuccess: ((AuthSession) -> Void)? = nil
    ) {
        self.onSuccess = onSuccess
        viewModel.obtainEvent(
            event: LoginEvent.OnLoginClick(
                username: user.name,
                password: user.password
            )
        )
    }
}
