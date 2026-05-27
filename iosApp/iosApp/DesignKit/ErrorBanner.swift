//
//  ErrorBanner.swift
//  iosApp
//
//  Created by Данил Забинский on 08.05.2026.
//

import SwiftUI

struct ErrorBanner: View {
    let message: String

    var body: some View {
        HStack(spacing: DesignTheme.Spacing.sm) {
            Image(systemName: "exclamationmark.circle.fill")
                .foregroundColor(DesignTheme.error)
                .font(.system(size: 16))

            Text(message)
                .font(DesignTheme.Typography.bodySmall)
                .foregroundColor(DesignTheme.error)

            Spacer()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(DesignTheme.Spacing.md)
        .background(DesignTheme.errorLight)
        .cornerRadius(DesignTheme.CornerRadius.small)
    }
}
