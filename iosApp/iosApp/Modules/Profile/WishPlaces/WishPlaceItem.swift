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
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.sm) {
                HStack(alignment: .top, spacing: DesignTheme.Spacing.md) {
                    VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                        Text(place.title)
                            .font(DesignTheme.Typography.body)
                            .fontWeight(.semibold)
                            .lineLimit(1)

                        if let location = place.location {
                            Text(location)
                                .font(DesignTheme.Typography.caption)
                                .foregroundColor(.gray)
                                .lineLimit(1)
                        }
                    }

                    Spacer()

                    Text(place.status.name.lowercased().prefix(1).uppercased() + place.status.name.lowercased().dropFirst())
                        .font(DesignTheme.Typography.bodySmallest)
                        .foregroundColor(DesignTheme.accentColor)
                }

                if let description = place.description_ {
                    Text(description)
                        .font(DesignTheme.Typography.caption)
                        .foregroundColor(.gray)
                        .lineLimit(1)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
        }
        .foregroundColor(.primary)
    }
}
