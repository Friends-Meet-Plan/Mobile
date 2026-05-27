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
        ScrollView {
            VStack(spacing: DesignTheme.Spacing.xxxl) {
                Image("OnboardingImage")
                    .resizable()
                    .scaledToFit()
                    .padding(.top, DesignTheme.Spacing.sm)
                    .padding(.horizontal, DesignTheme.Spacing.sm)
                    .frame(maxWidth: .infinity)
                
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

                    if let error = reducer.errorMessage {
                        FormErrorMessage(message: error)
                    }
                }
                .padding(.horizontal, DesignTheme.Spacing.xl)
                
                ButtonFactory.primaryLoading(
                    action: { reducer.login() },
                    label: "Login",
                    isLoading: reducer.isLoading,
                    isEnabled: !(reducer.isLoading || reducer.username.isEmpty || reducer.password.isEmpty)
                )
                .padding(.horizontal, DesignTheme.Spacing.xl)
                
                HStack(spacing: DesignTheme.Spacing.xs) {
                    Text("Don't have an account?")
                        .font(DesignTheme.Typography.caption)
                        .foregroundColor(.gray)

                    Button(action: { isRegisterPresented = true }) {
                        Text("Sign up")
                            .font(DesignTheme.Typography.captionSemibold)
                            .foregroundColor(DesignTheme.accentColorHex)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .center)
                
                Spacer(minLength: DesignTheme.Spacing.xl)
            }
            .padding(.top, DesignTheme.Spacing.md)
        }
        .onAppear {
            reducer.onLoginSuccess = onLoginSuccess
        }
        .sheet(isPresented: $isRegisterPresented) {
            RegisterView(onRegisterSuccess: {
                isRegisterPresented = false
            })
            .presentationDetents([.medium, .large])
        }
    }
}
