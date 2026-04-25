//
//  RegisterView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

struct RegisterView: View {

    var onRegisterSuccess: (() -> Void)?
    
    @State private var observable = RegisterReducer()
    @State private var user = User()

    var body: some View {
        VStack(spacing: 16) {
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
                observable.register(user: user) {
                    onRegisterSuccess?()
                }
            } label: {
                if observable.isLoading {
                    ProgressView()
                        .tint(.white)
                        .frame(maxWidth: .infinity)
                } else {
                    Text("Register")
                        .frame(maxWidth: .infinity)
                }
            }
            .buttonStyle(.borderedProminent)
            .disabled(observable.isLoading || user.name.isEmpty || user.password.isEmpty)
        }
        .padding()
    }
}
