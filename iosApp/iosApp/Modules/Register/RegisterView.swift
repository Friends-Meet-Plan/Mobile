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
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        ScrollView {
            VStack(spacing: DesignTheme.Spacing.xxl) {
                HStack {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Create Account")
                            .font(DesignTheme.Typography.heading)
                            .foregroundColor(.black)
                    }
                    Spacer()
                    Button(action: { dismiss() }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 28))
                            .foregroundColor(.gray.opacity(0.5))
                    }
                }
                .padding(.horizontal, DesignTheme.Spacing.xl)
                .padding(.top, DesignTheme.Spacing.lg)
                
                VStack(spacing: DesignTheme.Spacing.lg) {
                    TextField("Username", text: .init(
                        get: { reducer.username },
                        set: { reducer.setUsername($0) }
                    ))
                    .formTextField()

                    SecureField("Password", text: .init(
                        get: { reducer.password },
                        set: { reducer.setPassword($0) }
                    ))
                    .formSecureField()

                    FormInfoMessage(message: "At least 8 characters")

                    if let error = reducer.errorMessage {
                        FormErrorMessage(message: error)
                    }
                }
                .padding(.horizontal, DesignTheme.Spacing.xl)
                
                Button(action: {
                    reducer.register()
                }) {
                    if reducer.isLoading {
                        HStack(spacing: DesignTheme.Spacing.sm) {
                            ProgressView()
                                .scaleEffect(0.9)
                                .tint(.white)
                            Text("Creating account...")
                                .font(DesignTheme.Typography.button)
                        }
                    } else {
                        Text("Create Account")
                            .font(DesignTheme.Typography.button)
                    }
                }
                .primaryButton(isLoading: reducer.isLoading, isEnabled: !(reducer.isLoading || reducer.username.isEmpty || reducer.password.isEmpty))
                .padding(.horizontal, DesignTheme.Spacing.xl)
                
                HStack(spacing: DesignTheme.Spacing.xs) {
                    Text("Already have an account?")
                        .font(DesignTheme.Typography.caption)
                        .foregroundColor(.gray)

                    Button(action: { dismiss() }) {
                        Text("Login")
                            .font(DesignTheme.Typography.captionSemibold)
                            .foregroundColor(DesignTheme.accentColorHex)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .center)

                Spacer(minLength: DesignTheme.Spacing.xl)
            }
        }
        .onAppear {
            reducer.onRegisterSuccess = onRegisterSuccess
        }
    }
}
