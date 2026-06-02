//
//  WishPlaceItem.swift
//  iosApp
//
//  Created by Данил Забинский on 16.05.2026.
//

import SwiftUI
import Shared

struct WishPlaceItem: View {
    
    let place: Shared.WishPlace
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack(alignment: .center, spacing: DesignTheme.Spacing.md) {
                VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                    Text(place.title)
                        .font(DesignTheme.Typography.body)
                        .fontWeight(.semibold)
                        .lineLimit(1)

                    if let location = place.location {
                        HStack(alignment: .center, spacing: DesignTheme.Spacing.xs) {
                            Image(systemName: "location.fill")
                                .font(.system(size: 12))
                                .foregroundColor(DesignTheme.accentColor)

                            Text(location)
                                .font(DesignTheme.Typography.caption)
                                .foregroundColor(.gray)
                                .lineLimit(1)
                        }
                    }
                }

                Spacer()

                Text(place.status.name.lowercased().prefix(1).uppercased() + place.status.name.lowercased().dropFirst())
                    .font(DesignTheme.Typography.bodySmallest)
                    .foregroundColor(.white)
                    .padding(.horizontal, DesignTheme.Spacing.sm)
                    .padding(.vertical, DesignTheme.Spacing.xs)
                    .background(DesignTheme.accentColor)
                    .cornerRadius(DesignTheme.CornerRadius.capsule)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(DesignTheme.Spacing.md)
            .background(Color(UIColor.systemGray6))
            .cornerRadius(DesignTheme.CornerRadius.medium)
            .contentShape(Rectangle())
        }
        .foregroundColor(.primary)
    }
}
