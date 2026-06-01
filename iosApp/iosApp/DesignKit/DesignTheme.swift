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
    static let error = Color(red: 1.0, green: 0.23, blue: 0.19)
    static let errorLight = Color.red.opacity(0.08)
    static let infoLight = Color.blue.opacity(0.06)
    static let secondaryAccent = Color.green

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

// MARK: - Button Factory

/// Factory for creating consistent, clickable buttons throughout the app.
/// Each factory method returns a complete Button view with proper hit area targeting.
enum ButtonFactory {
    /// Primary action button with full width, blue capsule background.
    /// The entire button area is clickable (54pt height).
    ///
    /// Usage:
    /// ```
    /// ButtonFactory.primary(
    ///     action: { viewModel.login() },
    ///     label: "Login",
    ///     isEnabled: !formEmpty
    /// )
    /// .padding(.horizontal, DesignTheme.Spacing.xl)
    /// ```
    @ViewBuilder
    static func primary(
        action: @escaping () -> Void,
        label: String,
        isEnabled: Bool = true
    ) -> some View {
        Button(action: action) {
            Text(label)
                .font(DesignTheme.Typography.button)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 54)
                .background(DesignTheme.accentColor, in: .capsule)
        }
        .disabled(!isEnabled)
        .opacity(isEnabled ? 1 : 0.3)
    }

    /// Primary action button with loading indicator.
    /// Shows a spinner and "Loading..." text while isLoading is true.
    /// The entire button area is clickable (54pt height).
    ///
    /// Usage:
    /// ```
    /// ButtonFactory.primaryLoading(
    ///     action: { reducer.login() },
    ///     label: "Login",
    ///     isLoading: reducer.isLoading,
    ///     isEnabled: !formEmpty
    /// )
    /// ```
    @ViewBuilder
    static func primaryLoading(
        action: @escaping () -> Void,
        label: String,
        isLoading: Bool = false,
        isEnabled: Bool = true
    ) -> some View {
        Button(action: action) {
            primaryContent(label: label, isLoading: isLoading)
                .frame(maxWidth: .infinity)
                .frame(height: 54)
                .background(DesignTheme.accentColor, in: .capsule)
        }
        .disabled(!isEnabled || isLoading)
        .opacity(isEnabled ? 1 : 0.3)
    }
    
    @ViewBuilder
    private static func primaryContent(label: String, isLoading: Bool) -> some View {
        if isLoading {
            HStack(spacing: DesignTheme.Spacing.sm) {
                ProgressView()
                    .scaleEffect(0.9)
                    .tint(.white)
                Text("Loading...")
                    .font(DesignTheme.Typography.button)
                    .foregroundColor(.white)
            }
        } else {
            Text(label)
                .font(DesignTheme.Typography.button)
                .foregroundColor(.white)
        }
    }

    /// Secondary action button with error/red color, light background.
    /// Typically used for destructive or decline actions.
    /// The entire button area is clickable (54pt height).
    ///
    /// Usage:
    /// ```
    /// ButtonFactory.secondary(
    ///     action: { reducer.reject() },
    ///     label: "Decline",
    ///     isEnabled: !isLoading
    /// )
    /// ```
    @ViewBuilder
    static func secondary(
        action: @escaping () -> Void,
        label: String,
        isEnabled: Bool = true
    ) -> some View {
        Button(action: action) {
            Text(label)
                .font(DesignTheme.Typography.button)
                .foregroundColor(DesignTheme.error)
                .frame(maxWidth: .infinity)
                .frame(height: 54)
                .background(DesignTheme.errorLight, in: .capsule)
        }
        .disabled(!isEnabled)
        .opacity(isEnabled ? 1 : 0.3)
    }

    /// Compact button for inline use (smaller height, no full width).
    /// Useful for secondary actions or buttons within HStacks.
    /// The entire button area is clickable (44pt height).
    ///
    /// Usage:
    /// ```
    /// HStack {
    ///     ButtonFactory.compact(
    ///         action: { dismiss() },
    ///         label: "Cancel"
    ///     )
    ///     ButtonFactory.compact(
    ///         action: { save() },
    ///         label: "Save"
    ///     )
    /// }
    /// ```
    @ViewBuilder
    static func compact(
        action: @escaping () -> Void,
        label: String,
        isEnabled: Bool = true
    ) -> some View {
        Button(action: action) {
            Text(label)
                .font(DesignTheme.Typography.bodySmall)
                .foregroundColor(DesignTheme.accentColor)
                .padding(.horizontal, DesignTheme.Spacing.md)
                .frame(height: 44)
                .background(Color(UIColor.systemGray6), in: .capsule)
        }
        .disabled(!isEnabled)
        .opacity(isEnabled ? 1 : 0.5)
    }

    /// Disabled status button (e.g., "Request Sent", read-only).
    /// Shows with a gray background and an optional icon.
    /// Not clickable; used to display state information.
    ///
    /// Usage:
    /// ```
    /// ButtonFactory.disabled(
    ///     label: "Request Sent",
    ///     icon: "checkmark.circle.fill"
    /// )
    /// ```
    @ViewBuilder
    static func disabled(
        label: String,
        icon: String? = nil
    ) -> some View {
        HStack(spacing: DesignTheme.Spacing.sm) {
            if let icon = icon {
                Image(systemName: icon)
                    .font(.system(size: 16))
            }
            Text(label)
                .font(DesignTheme.Typography.button)
        }
        .foregroundColor(.white)
        .frame(maxWidth: .infinity)
        .frame(height: 54)
        .background(Color.gray.opacity(0.6), in: .capsule)
    }

    /// Destructive action button with red background.
    /// Used for permanent actions like "Remove Friend", "Delete".
    ///
    /// Usage:
    /// ```
    /// ButtonFactory.destructive(
    ///     action: { reducer.removeFriend() },
    ///     label: "Remove Friend",
    ///     isLoading: isLoading
    /// )
    /// ```
    @ViewBuilder
    static func destructive(
        action: @escaping () -> Void,
        label: String,
        isLoading: Bool = false
    ) -> some View {
        Button(action: action) {
            content(isLoading: isLoading, label: label)
                .frame(maxWidth: .infinity)
                .frame(height: 54)
                .background(DesignTheme.error, in: .capsule)
        }
        .disabled(isLoading)
    }
    
    @ViewBuilder
    private static func content(isLoading: Bool, label: String) -> some View {
        if isLoading {
            HStack(spacing: DesignTheme.Spacing.sm) {
                ProgressView()
                    .scaleEffect(0.9)
                    .tint(.white)
                Text("Removing...")
                    .font(DesignTheme.Typography.button)
                    .foregroundColor(.white)
            }
        } else {
            Text(label)
                .font(DesignTheme.Typography.button)
                .foregroundColor(.white)
        }
    }
}

// MARK: - Indicator Factory

/// Factory for creating consistent status indicators and badges throughout the app.
enum IndicatorFactory {
    /// Green checkmark badge with "Active" label.
    /// Used to indicate active friendship status.
    @ViewBuilder
    static func active() -> some View {
        HStack(spacing: DesignTheme.Spacing.xs) {
            Image(systemName: "checkmark.circle.fill")
                .font(.system(size: 16))
                .foregroundColor(DesignTheme.secondaryAccent)
            Text("Active")
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(DesignTheme.secondaryAccent)
        }
        .padding(.horizontal, DesignTheme.Spacing.sm)
        .padding(.vertical, DesignTheme.Spacing.xs)
        .background(DesignTheme.secondaryAccent.opacity(0.1))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }

    /// Orange clock badge with "Pending" label.
    /// Used to indicate pending friend request from current user's perspective.
    @ViewBuilder
    static func pending() -> some View {
        HStack(spacing: DesignTheme.Spacing.xs) {
            Image(systemName: "clock.fill")
                .font(.system(size: 16))
                .foregroundStyle(.background)
            Text("Pending")
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(.white)
        }
        .padding(.horizontal, DesignTheme.Spacing.sm)
        .padding(.vertical, DesignTheme.Spacing.xs)
        .background(Color.orange)
        .cornerRadius(DesignTheme.CornerRadius.small)
    }

    /// Green checkmark badge with "Accepted" label.
    /// Used to indicate accepted friend request.
    @ViewBuilder
    static func accepted() -> some View {
        HStack(spacing: DesignTheme.Spacing.xs) {
            Image(systemName: "checkmark.circle.fill")
                .font(.system(size: 16))
                .foregroundColor(DesignTheme.secondaryAccent)
            Text("Accepted")
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(DesignTheme.secondaryAccent)
        }
        .padding(.horizontal, DesignTheme.Spacing.sm)
        .padding(.vertical, DesignTheme.Spacing.xs)
        .background(DesignTheme.secondaryAccent.opacity(0.1))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }

    /// Red X badge with "Declined" label.
    /// Used to indicate declined/rejected friend request.
    @ViewBuilder
    static func declined() -> some View {
        HStack(spacing: DesignTheme.Spacing.xs) {
            Image(systemName: "xmark.circle.fill")
                .font(.system(size: 16))
                .foregroundColor(DesignTheme.error)
            Text("Declined")
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(DesignTheme.error)
        }
        .padding(.horizontal, DesignTheme.Spacing.sm)
        .padding(.vertical, DesignTheme.Spacing.xs)
        .background(DesignTheme.errorLight)
        .cornerRadius(DesignTheme.CornerRadius.small)
    }

    /// Gray badge with "Sent" label.
    /// Used to indicate outgoing friend request (sent by user).
    @ViewBuilder
    static func sent() -> some View {
        HStack(spacing: DesignTheme.Spacing.xs) {
            Image(systemName: "paperplane.fill")
                .font(.system(size: 16))
                .foregroundColor(.gray)
            Text("Sent")
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(.gray)
        }
        .padding(.horizontal, DesignTheme.Spacing.sm)
        .padding(.vertical, DesignTheme.Spacing.xs)
        .background(Color(.systemGray6))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }

    /// Generic status indicator with custom label, color, and icon.
    /// Use this for status badges that don't fit the predefined patterns.
    ///
    /// Usage:
    /// ```
    /// IndicatorFactory.status(
    ///     label: "Custom Status",
    ///     color: Color.purple,
    ///     icon: "star.fill"
    /// )
    /// ```
    @ViewBuilder
    static func status(
        label: String,
        color: Color,
        icon: String
    ) -> some View {
        HStack(spacing: DesignTheme.Spacing.xs) {
            Image(systemName: icon)
                .font(.system(size: 16))
                .foregroundColor(color)
            Text(label)
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(color)
        }
        .padding(.horizontal, DesignTheme.Spacing.sm)
        .padding(.vertical, DesignTheme.Spacing.xs)
        .background(color.opacity(0.1))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }
}

// MARK: - Form Field Modifiers

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

// MARK: - DEPRECATED: PrimaryButton Modifier

/// DEPRECATED: Use ButtonFactory.primary() instead.
/// This modifier had limited clickability (only text area was clickable).
/// The factory methods properly handle full-area click targets.
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

// MARK: - View Extensions

extension View {
    func formTextField() -> some View {
        modifier(FormTextField())
    }

    func formSecureField() -> some View {
        modifier(FormSecureField())
    }

    /// DEPRECATED: Use ButtonFactory instead for proper clickable buttons.
    /// Example migration:
    /// ```
    /// // Old (deprecated):
    /// Button { ... }
    ///     .primaryButton(isLoading: isLoading, isEnabled: isEnabled)
    ///
    /// // New (recommended):
    /// ButtonFactory.primaryLoading(
    ///     action: { ... },
    ///     label: "Login",
    ///     isLoading: isLoading,
    ///     isEnabled: isEnabled
    /// )
    /// ```
    func primaryButton(isLoading: Bool = false, isEnabled: Bool = true) -> some View {
        modifier(PrimaryButton(isLoading: isLoading, isEnabled: isEnabled))
    }

    @ViewBuilder
    func `if`<Content: View>(_ condition: Bool, @ViewBuilder content: (Self) -> Content) -> some View {
        if condition {
            content(self)
        } else {
            self
        }
    }
}
