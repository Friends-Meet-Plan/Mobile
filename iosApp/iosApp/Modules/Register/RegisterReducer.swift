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

    deinit {
        viewModel.dispose()
    }

    func register(
        user: User,
        onSuccess: (() -> Void)? = nil
    ) {
        isLoading = true
        errorMessage = nil
        
        viewModel.register(
            username: user.name,
            password: user.password,
            onSuccess: { [weak self] in
                self?.isLoading = false
                onSuccess?()
            },
            onError: { [weak self] message in
                self?.isLoading = false
                self?.errorMessage = message
            }
        )
    }
}
