//
//  LoginView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct LoginView: View {
    @State private var reducer = LoginReducer()
    
    let onLoginSuccess: (AuthSession) -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            TextField("Username", text: $reducer.username)
                .textFieldStyle(.roundedBorder)
                .autocapitalization(.none)
            
            SecureField("Password", text: $reducer.password)
                .textFieldStyle(.roundedBorder)
            
            if let error = reducer.errorMessage {
                Text(error).foregroundColor(.red).font(.caption)
            }
            
            Button("Login") { reducer.login() }
                .disabled(reducer.username.isEmpty || reducer.password.isEmpty || reducer.isLoading)
            
            if reducer.isLoading {
                ProgressView()
            }
        }
        .padding()
        .onAppear {
            reducer.onLoginSuccess = onLoginSuccess
        }
    }
}
