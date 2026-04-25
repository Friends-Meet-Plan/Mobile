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

    deinit {
        viewModel.dispose()
    }

    func login(
        user: User,
        onSuccess: ((AuthSession) -> Void)? = nil
    ) {
        isLoading = true
        errorMessage = nil
        
        viewModel.login(
            username: user.name,
            password: user.password,
            onSuccess: { [weak self] session in
                self?.isLoading = false
                onSuccess?(session)
            },
            onError: { [weak self] message in
                self?.isLoading = false
                self?.errorMessage = message
            }
        )
    }
}
