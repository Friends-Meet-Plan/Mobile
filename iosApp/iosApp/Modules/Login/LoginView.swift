//
//  LoginView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

struct LoginView: View {

    var onLoginSuccess: ((User) -> Void)?

    @State private var observable = LoginReducer()
    @State private var user = User()
    @State private var isRegistrationPresented = false

    var body: some View {
        VStack(spacing: 16) {
            loginView()
            
            Text("Register now")
                .onTapGesture {
                    isRegistrationPresented = true
                }
        }
        .padding()
        .sheet(isPresented: $isRegistrationPresented) {
            RegisterView {
                // TODO: Toast
                isRegistrationPresented = false
            }
        }
    }
    
    @ViewBuilder
    private func loginView() -> some View {
        TextField("Username", text: $user.name)
            .textFieldStyle(.roundedBorder)
            .autocorrectionDisabled()
            .textInputAutocapitalization(.never)

        SecureField("Password", text: $user.password)
            .textFieldStyle(.roundedBorder)

        if let error = observable.errorMessage {
            Text(error)
                .foregroundStyle(.red)
                .font(.caption)
        }

        Button {
            observable.login(user: user) { session in
                onLoginSuccess?(User(name: session.user.username))
            }
        } label: {
            if observable.isLoading {
                ProgressView()
                    .tint(.white)
                    .frame(maxWidth: .infinity)
            } else {
                Text("Login")
                    .frame(maxWidth: .infinity)
            }
        }
        .buttonStyle(.borderedProminent)
        .disabled(observable.isLoading || user.name.isEmpty || user.password.isEmpty)
    }
}
