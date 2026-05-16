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
    let canDelete: Bool
    let onTap: () -> Void
    let onDelete: () -> Void
    
    @State private var dismissOffset: CGFloat = 0
    private let dismissThreshold: CGFloat = UIScreen.main.bounds.width * 0.3
    
    var body: some View {
        ZStack(alignment: .trailing) {
            if canDelete {
                HStack(spacing: 0) {
                    Spacer()
                    VStack {
                        Image(systemName: "trash.fill")
                            .font(.system(size: 18))
                            .foregroundColor(.red)
                    }
                    .background(Color.clear)
                }
            }
            
            VStack(alignment: .leading, spacing: 8) {
                HStack(alignment: .top, spacing: 12) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(place.title)
                            .font(.system(.body, design: .default))
                            .fontWeight(.semibold)
                            .lineLimit(2)
                        
                        if let location = place.location {
                            HStack(spacing: 4) {
                                Image(systemName: "location.fill")
                                    .font(.system(size: 12))
                                    .foregroundColor(.blue)
                                Text(location)
                                    .font(.caption)
                                    .foregroundColor(.gray)
                                    .lineLimit(1)
                            }
                        }
                    }
                    
                    Spacer()
                    
                    Text(place.status.name.lowercased().prefix(1).uppercased() + place.status.name.lowercased().dropFirst())
                        .font(.caption2)
                        .fontWeight(.medium)
                        .foregroundColor(.blue)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(4)
                }
                
                if let description = place.description_ {
                    Text(description)
                        .font(.caption)
                        .foregroundColor(.gray)
                        .lineLimit(2)
                }
            }
            .padding(12)
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .offset(x: dismissOffset)
            .gesture(
                canDelete ? DragGesture()
                    .onChanged { gesture in
                        let translation = gesture.translation.width
                        if translation < 0 {
                            dismissOffset = min(0, translation)
                        }
                    }
                    .onEnded { gesture in
                        if gesture.translation.width < -dismissThreshold {
                            dismissOffset = 0
                            onDelete()
                        } else {
                            withAnimation(.easeOut(duration: 0.2)) {
                                dismissOffset = 0
                            }
                        }
                    }
                : nil
            )
            .onTapGesture {
                onTap()
            }
        }
        .clipped()
        .cornerRadius(12)
    }
}
