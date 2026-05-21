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
            VStack(spacing: 32) {
                Image("OnboardingImage")
                    .resizable()
                    .scaledToFit()
                    .padding(.top, 8)
                    .padding(.horizontal, 8)
                    .frame(maxWidth: .infinity)
                
                VStack(spacing: 16) {
                    TextField("Username", text: .init(
                        get: { reducer.username },
                        set: { reducer.setUsername($0) }
                    ))
                    .textFieldStyle(.plain)
                    .padding(14)
                    .background(Color(UIColor.systemGray6))
                    .cornerRadius(12)
                    .font(.system(size: 16, weight: .regular, design: .default))
                    
                    SecureField("Password", text: .init(
                        get: { reducer.password },
                        set: { reducer.setPassword($0) }
                    ))
                    .textFieldStyle(.plain)
                    .padding(14)
                    .background(Color(UIColor.systemGray6))
                    .cornerRadius(12)
                    .font(.system(size: 16, weight: .regular, design: .default))
                    
                    if let error = reducer.errorMessage {
                        HStack(spacing: 10) {
                            Image(systemName: "exclamationmark.circle.fill")
                                .foregroundColor(.red)
                            Text(error)
                                .font(.system(size: 14, weight: .regular, design: .default))
                                .foregroundColor(.red)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(12)
                        .background(Color.red.opacity(0.08))
                        .cornerRadius(10)
                    }
                }
                .padding(.horizontal, 20)
                
                Button(action: {
                    reducer.login()
                }) {
                    if reducer.isLoading {
                        HStack(spacing: 10) {
                            ProgressView()
                                .scaleEffect(0.9)
                                .tint(.white)
                            Text("Logging in...")
                                .font(.system(size: 16, weight: .semibold, design: .default))
                        }
                    } else {
                        Text("Login")
                            .font(.system(size: 16, weight: .semibold, design: .default))
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 54)
                .foregroundColor(.white)
                .background(Color.blue.opacity(0.5), in: .capsule)
                .disabled(reducer.isLoading || reducer.username.isEmpty || reducer.password.isEmpty)
                .opacity((reducer.isLoading || reducer.username.isEmpty || reducer.password.isEmpty) ? 0.6 : 1)
                .padding(.horizontal, 20)
                
                HStack(spacing: 4) {
                    Text("Don't have an account?")
                        .font(.system(size: 15, weight: .regular, design: .default))
                        .foregroundColor(.gray)
                    
                    Button(action: { isRegisterPresented = true }) {
                        Text("Sign up")
                            .font(.system(size: 15, weight: .semibold, design: .default))
                            .foregroundColor(Color(red: 0.0, green: 0.48, blue: 1.0))
                    }
                }
                .frame(maxWidth: .infinity, alignment: .center)
                
                Spacer(minLength: 20)
            }
            .padding(.top, 12)
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
