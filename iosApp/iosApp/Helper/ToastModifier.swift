//
//  ToastModifier.swift
//  iosApp
//
//  Created by Данил Забинский on 18.05.2026.
//

import SwiftUI

struct ToastModifier: ViewModifier {
    @State var isPresented: Bool
    let message: String

    func body(content: Content) -> some View {
        ZStack {
            content

            if isPresented {
                VStack {
                    Text(message)
                        .font(.subheadline)
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color.primary.opacity(0.8))
                        .cornerRadius(8)
                        .padding()

                    Spacer()
                }
                .transition(.opacity)
            }
        }
    }
}

extension View {
    func toast(isPresented: Binding<Bool>, message: String) -> some View {
        modifier(ToastModifier(isPresented: isPresented.wrappedValue, message: message))
    }
}
