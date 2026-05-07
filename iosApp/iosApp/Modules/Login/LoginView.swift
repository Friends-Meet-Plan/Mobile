//
//  LoginView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct LoginView: View {
    let onLoginSuccess: (AuthSession) -> Void
    
    @State private var reducer = LoginReducer()
    @State private var isRegisterPresented = false
    
    var body: some View {
        VStack(spacing: 16) {
            TextField("Username", text: $reducer.username)
            SecureField("Password", text: $reducer.password)
            
            if let error = reducer.errorMessage {
                Text(error).foregroundColor(.red).font(.caption)
            }
            
            Button("Login") {
                reducer.login()
            }
            .disabled(reducer.isLoading)
            
            Button("Register now") {
                isRegisterPresented = true
            }
            
            if reducer.isLoading {
                ProgressView()
            }
        }
        .onAppear {
            reducer.onLoginSuccess = onLoginSuccess
        }
        .sheet(isPresented: $isRegisterPresented) {
            RegisterView {
                isRegisterPresented = false
            }
        }
    }
}
