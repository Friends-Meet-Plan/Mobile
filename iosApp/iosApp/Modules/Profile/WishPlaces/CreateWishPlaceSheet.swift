//
//  CreateWishPlaceSheet.swift
//  iosApp
//
//  Created by Данил Забинский on 16.05.2026.
//

import SwiftUI

struct CreateWishPlaceSheet: View {

    @State private var title = ""
    @State private var description = ""
    @State private var location = ""
    @State private var link = ""

    let onDismiss: () -> Void
    let onCreate: (String, String?, String?, String?) -> Void

    var isCreateButtonEnabled: Bool {
        !title.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 16) {
                    TextField("Title *", text: $title)
                        .textFieldStyle(.roundedBorder)
                        .padding(.horizontal)

                    TextField("Location", text: $location)
                        .textFieldStyle(.roundedBorder)
                        .padding(.horizontal)

                    TextField("Description", text: $description, axis: .vertical)
                        .textFieldStyle(.roundedBorder)
                        .frame(minHeight: 80, maxHeight: 120)
                        .padding(.horizontal)

                    TextField("Link", text: $link)
                        .textFieldStyle(.roundedBorder)
                        .padding(.horizontal)

                    Spacer()
                        .frame(height: 16)

                    Button(action: {
                        onCreate(
                            title,
                            description.isEmpty ? nil : description,
                            location.isEmpty ? nil : location,
                            link.isEmpty ? nil : link
                        )
                    }) {
                        Text("Create")
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(isCreateButtonEnabled ? Color.blue : Color.gray)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                    .disabled(!isCreateButtonEnabled)
                    .padding(.horizontal)

                    Spacer()
                        .frame(height: 32)
                }
                .padding(.vertical, 16)
            }
            .navigationTitle("Add Wish Place")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        onDismiss()
                    }
                }
            }
        }
    }
}

#Preview {
    CreateWishPlaceSheet(
        onDismiss: {},
        onCreate: { _, _, _, _ in }
    )
}
