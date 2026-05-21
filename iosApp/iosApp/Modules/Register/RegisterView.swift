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
            VStack(spacing: 28) {
                HStack {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Create Account")
                            .font(.system(size: 28, weight: .bold, design: .default))
                            .foregroundColor(.black)
                    }
                    Spacer()
                    Button(action: { dismiss() }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 28))
                            .foregroundColor(.gray.opacity(0.5))
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 16)
                
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
                    
                    HStack(spacing: 8) {
                        Image(systemName: "info.circle.fill")
                            .font(.system(size: 13))
                            .foregroundColor(Color(red: 0.0, green: 0.48, blue: 1.0))
                        Text("At least 8 characters")
                            .font(.system(size: 13, weight: .regular, design: .default))
                            .foregroundColor(Color(red: 0.0, green: 0.48, blue: 1.0).opacity(0.8))
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(12)
                    .background(Color(red: 0.0, green: 0.48, blue: 1.0).opacity(0.06))
                    .cornerRadius(10)
                    
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
                    reducer.register()
                }) {
                    if reducer.isLoading {
                        HStack(spacing: 10) {
                            ProgressView()
                                .scaleEffect(0.9)
                                .tint(.white)
                            Text("Creating account...")
                                .font(.system(size: 16, weight: .semibold, design: .default))
                        }
                    } else {
                        Text("Create Account")
                            .font(.system(size: 16, weight: .semibold, design: .default))
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 54)
                .foregroundColor(.white)
                .background(Color(red: 0.0, green: 0.48, blue: 1.0)) // Pure blue
                .cornerRadius(27)
                .disabled(reducer.isLoading || reducer.username.isEmpty || reducer.password.isEmpty)
                .opacity((reducer.isLoading || reducer.username.isEmpty || reducer.password.isEmpty) ? 0.6 : 1)
                .padding(.horizontal, 20)
                
                HStack(spacing: 4) {
                    Text("Already have an account?")
                        .font(.system(size: 15, weight: .regular, design: .default))
                        .foregroundColor(.gray)
                    
                    Button(action: { dismiss() }) {
                        Text("Login")
                            .font(.system(size: 15, weight: .semibold, design: .default))
                            .foregroundColor(Color(red: 0.0, green: 0.48, blue: 1.0))
                    }
                }
                .frame(maxWidth: .infinity, alignment: .center)
                
                Spacer(minLength: 20)
            }
        }
        .onAppear {
            reducer.onRegisterSuccess = onRegisterSuccess
        }
    }
}

#Preview {
    RegisterView(onRegisterSuccess: {})
}
