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
                VStack(spacing: DesignTheme.Spacing.lg) {
                    TextField("Title *", text: $title)
                        .placeholder(when: title.isEmpty) {
                            Text("Title *").foregroundColor(.gray)
                        }
                        .formTextField()
                    
                    TextField("Location", text: $location)
                        .placeholder(when: location.isEmpty) {
                            Text("Location").foregroundColor(.gray)
                        }
                        .formTextField()
                    
                    TextField("Description", text: $description, axis: .vertical)
                        .placeholder(when: description.isEmpty) {
                            Text("Description").foregroundColor(.gray)
                        }
                        .formTextField()
                    
                    TextField("Link", text: $link)
                        .placeholder(when: link.isEmpty) {
                            Text("Link").foregroundColor(.gray)
                        }
                        .formTextField()
                        .padding(.bottom, DesignTheme.Spacing.md)
                    
                    ButtonFactory.primary(
                        action: {
                            onCreate(
                                title,
                                description.isEmpty ? nil : description,
                                location.isEmpty ? nil : location,
                                link.isEmpty ? nil : link
                            )
                        },
                        label: "Create",
                        isEnabled: isCreateButtonEnabled
                    )
                    .padding(.horizontal, DesignTheme.Spacing.lg)
                    
                    Spacer()
                        .frame(height: DesignTheme.Spacing.xxxl)
                }
                .padding(.vertical, DesignTheme.Spacing.lg)
                .padding(.horizontal, DesignTheme.Spacing.lg)
            }
            .background(Color(UIColor.systemBackground))
            .navigationTitle("Add Wish Place")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        onDismiss()
                    }
                    .foregroundColor(DesignTheme.accentColor)
                }
            }
        }
    }
}

extension View {
    func placeholder<Content: View>(when shouldShow: Bool, alignment: Alignment = .leading, @ViewBuilder placeholder: () -> Content) -> some View {
        ZStack(alignment: alignment) {
            placeholder().opacity(shouldShow ? 1 : 0)
            self
        }
    }
}
