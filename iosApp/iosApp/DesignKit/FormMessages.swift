//
//  FormMessages.swift
//  iosApp
//
//  Created by Данил Забинский on 21.05.2026.
//

import SwiftUI

struct FormErrorMessage: View {
    let message: String

    var body: some View {
        HStack(spacing: DesignTheme.Spacing.sm) {
            Image(systemName: "exclamationmark.circle.fill")
                .foregroundColor(.red)
            Text(message)
                .font(DesignTheme.Typography.bodySmall)
                .foregroundColor(.red)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(DesignTheme.Spacing.md)
        .background(Color.red.opacity(0.08))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }
}

struct FormInfoMessage: View {
    let message: String

    var body: some View {
        HStack(spacing: DesignTheme.Spacing.sm) {
            Image(systemName: "info.circle.fill")
                .font(.system(size: 13))
                .foregroundColor(DesignTheme.accentColorHex)
            Text(message)
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(DesignTheme.accentColorHex.opacity(0.8))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(DesignTheme.Spacing.md)
        .background(DesignTheme.accentColorHex.opacity(0.06))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }
}
