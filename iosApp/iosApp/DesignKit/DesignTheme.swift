//
//  DesignTheme.swift
//  iosApp
//
//  Created by Данил Забинский on 21.05.2026.
//

import SwiftUI
import UIKit

enum DesignTheme {
    static let accentColor = Color.blue
    static let accentColorHex = Color(red: 0.0, green: 0.48, blue: 1.0)

    enum Spacing {
        static let xs: CGFloat = 4
        static let sm: CGFloat = 8
        static let md: CGFloat = 12
        static let lg: CGFloat = 16
        static let xl: CGFloat = 20
        static let xxl: CGFloat = 28
        static let xxxl: CGFloat = 32
    }

    enum Typography {
        static let body = Font.system(size: 16, weight: .regular, design: .default)
        static let bodySmall = Font.system(size: 14, weight: .regular, design: .default)
        static let bodySmallest = Font.system(size: 13, weight: .regular, design: .default)
        static let button = Font.system(size: 16, weight: .semibold, design: .default)
        static let caption = Font.system(size: 15, weight: .regular, design: .default)
        static let captionSemibold = Font.system(size: 15, weight: .semibold, design: .default)
        static let heading = Font.system(size: 28, weight: .bold, design: .default)
    }

    enum CornerRadius {
        static let small: CGFloat = 10
        static let medium: CGFloat = 12
        static let capsule: CGFloat = 27
    }
}

struct FormTextField: ViewModifier {
    func body(content: Content) -> some View {
        content
            .textFieldStyle(.plain)
            .padding(DesignTheme.Spacing.md)
            .background(Color(UIColor.systemGray6))
            .cornerRadius(DesignTheme.CornerRadius.medium)
            .font(DesignTheme.Typography.body)
    }
}

struct FormSecureField: ViewModifier {
    func body(content: Content) -> some View {
        content
            .textFieldStyle(.plain)
            .padding(DesignTheme.Spacing.md)
            .background(Color(UIColor.systemGray6))
            .cornerRadius(DesignTheme.CornerRadius.medium)
            .font(DesignTheme.Typography.body)
    }
}

struct PrimaryButton: ViewModifier {
    let isLoading: Bool
    let isEnabled: Bool

    func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity)
            .frame(height: 54)
            .foregroundColor(.white)
            .background(DesignTheme.accentColor, in: .capsule)
            .disabled(!isEnabled)
            .opacity(isEnabled ? 1 : 0.3)
    }
}

extension View {
    func formTextField() -> some View {
        modifier(FormTextField())
    }

    func formSecureField() -> some View {
        modifier(FormSecureField())
    }

    func primaryButton(isLoading: Bool = false, isEnabled: Bool = true) -> some View {
        modifier(PrimaryButton(isLoading: isLoading, isEnabled: isEnabled))
    }
}
