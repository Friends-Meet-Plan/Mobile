//
//  WishPlaceDetailSheet.swift
//  iosApp
//
//  Created by Данил Забинский on 16.05.2026.
//

import SwiftUI
import Shared

struct WishPlaceDetailSheet: View {
    
    let place: Shared.WishPlace
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
            HStack {
                Text(place.title)
                    .font(DesignTheme.Typography.heading)
                    .lineLimit(1)
                    .padding(.top, DesignTheme.Spacing.lg)
                
                Spacer()
                
                Button(action: { dismiss() }) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 20))
                        .foregroundColor(.gray)
                }
            }
            
            if let location = place.location {
                HStack(spacing: DesignTheme.Spacing.xs) {
                    Image(systemName: "mappin.circle.fill")
                        .foregroundColor(DesignTheme.accentColor)
                        .font(.system(size: 14))
                    Text(location)
                        .font(DesignTheme.Typography.body)
                        .foregroundColor(.primary)
                        .lineLimit(1)
                }
            }
            
            if let description = place.description_ {
                Text(description)
                    .font(DesignTheme.Typography.body)
                    .foregroundColor(.primary)
                    .lineLimit(2)
            }
            
            HStack(spacing: DesignTheme.Spacing.md) {
                StatusBadge(status: place.status.name)
                
                if let link = place.link, !link.isEmpty {
                    Link(destination: URL(string: link) ?? URL(fileURLWithPath: "")) {
                        HStack(spacing: DesignTheme.Spacing.xs) {
                            Image(systemName: "arrow.up.right")
                                .font(.system(size: 12, weight: .semibold))
                            Text("Link")
                                .font(DesignTheme.Typography.bodySmall)
                        }
                        .foregroundColor(.white)
                        .padding(.horizontal, DesignTheme.Spacing.md)
                        .padding(.vertical, DesignTheme.Spacing.xs)
                        .background(DesignTheme.accentColor)
                        .cornerRadius(DesignTheme.CornerRadius.capsule)
                    }
                }
                
                Spacer()
            }
            
            Text("Added \(place.createdAt.prefix(10))")
                .font(DesignTheme.Typography.bodySmallest)
                .foregroundColor(.gray)
        }
        .padding(DesignTheme.Spacing.lg)
        .background(Color(.systemBackground))
        .cornerRadius(DesignTheme.CornerRadius.capsule)
        .presentationDetents([.height(175)])
        .presentationBackground(.white)
    }
}

private struct StatusBadge: View {
    let status: String
    
    var body: some View {
        Text(status.lowercased().prefix(1).uppercased() + status.lowercased().dropFirst())
            .font(DesignTheme.Typography.bodySmallest)
            .foregroundColor(.white)
            .padding(.horizontal, DesignTheme.Spacing.md)
            .padding(.vertical, DesignTheme.Spacing.xs)
            .background(DesignTheme.accentColor)
            .cornerRadius(DesignTheme.CornerRadius.capsule)
    }
}
