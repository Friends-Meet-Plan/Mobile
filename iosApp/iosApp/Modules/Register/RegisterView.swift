//
//  RegisterView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

struct RegisterView: View {
    let onRegisterSuccess: () -> Void
    
    @State private var reducer = RegisterReducer()
    
    var body: some View {
        VStack(spacing: 16) {
            TextField("Username", text: $reducer.username)
            SecureField("Password", text: $reducer.password)
            
            if let error = reducer.errorMessage {
                Text(error).foregroundColor(.red).font(.caption)
            }
            
            Button("Register") {
                reducer.register()
            }
            .disabled(reducer.isLoading)
            
            if reducer.isLoading {
                ProgressView()
            }
        }
        .onAppear {
            reducer.onRegisterSuccess = onRegisterSuccess
        }
    }
}
