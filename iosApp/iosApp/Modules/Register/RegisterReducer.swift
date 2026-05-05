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

    var isLoading = false
    var errorMessage: String?

    private let viewModel = RegisterViewModel()
    private var stateJob: Kotlinx_coroutines_coreJob?
    private var actionJob: Kotlinx_coroutines_coreJob?
    private var onSuccess: (() -> Void)?

    init() {
        stateJob = viewModel.viewStates.subscribe(
            onItem: { [weak self] item in
                guard let state = item as? RegisterViewState else { return }
                self?.isLoading = state is RegisterViewState.Loading
                self?.errorMessage = (state as? RegisterViewState.Error)?.message
            },
            onComplete: {},
            onThrow: { _ in },
        )

        actionJob = viewModel.viewActions.subscribe(
            onItem: { [weak self] item in
                guard let action = item as? RegisterAction else { return }
                if action is RegisterAction.RegisterSucceeded {
                    self?.onSuccess?()
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

    func register(
        user: User,
        onSuccess: (() -> Void)? = nil
    ) {
        self.onSuccess = onSuccess
        viewModel.obtainEvent(
            event: RegisterEvent.OnRegisterClick(
                username: user.name,
                password: user.password
            )
        )
    }
}
