//
//  SearchBar.swift
//  iosApp
//
//  Created by Данил Забинский on 08.05.2026.
//

import SwiftUI

struct SearchBar: View {
    @Binding var text: String
    var onSearch: (String) -> Void
    var onClear: () -> Void

    var body: some View {
        HStack(spacing: DesignTheme.Spacing.sm) {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.secondary)
                .font(.system(size: 16))

            TextField("Search users", text: $text)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .font(DesignTheme.Typography.body)
                .onChange(of: text) { oldValue, newValue in
                    if newValue.isEmpty {
                        onClear()
                    } else {
                        onSearch(newValue)
                    }
                }

            if !text.isEmpty {
                Button(action: {
                    text = ""
                    onClear()
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.secondary)
                        .font(.system(size: 16))
                }
            }
        }
        .padding(DesignTheme.Spacing.md)
        .background(Color(.systemGray6))
        .cornerRadius(DesignTheme.CornerRadius.medium)
    }
}
