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
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    Text(place.title)
                        .font(.title2)
                        .fontWeight(.bold)
                    Divider()
                    DetailRow(label: "Location", value: place.location ?? "Not specified")
                    DetailRow(label: "Description", value: place.description ?? "No description")
                    if let link = place.link, !link.isEmpty {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Link")
                                .font(.caption)
                                .fontWeight(.semibold)
                                .foregroundColor(.blue)
                            Link(link, destination: URL(string: link) ?? URL(fileURLWithPath: ""))
                                .font(.body)
                                .lineLimit(1)
                        }
                    }
                    DetailRow(label: "Status", value: place.status.name)
                    DetailRow(label: "Added", value: place.createdAt)
                    Spacer()
                }
                .padding()
            }
            .navigationTitle("Wish Place Details")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
        }
    }
}

private struct DetailRow: View {
    let label: String
    let value: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.caption)
                .fontWeight(.semibold)
                .foregroundColor(.blue)
            Text(value)
                .font(.body)
                .foregroundColor(.primary)
        }
    }
}
