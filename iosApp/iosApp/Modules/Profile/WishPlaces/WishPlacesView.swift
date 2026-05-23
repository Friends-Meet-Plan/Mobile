//
//  WishPlacesView.swift
//  iosApp
//
//  Created by Данил Забинский on 16.05.2026.
//

import SwiftUI
import Shared

struct WishPlacesView: View {
    
    @State private var reducer: WishPlacesReducer
    private let mode: WishPlacesMode
    private let userId: String
    
    init(userId: String, mode: WishPlacesMode = .editable) {
        self.userId = userId
        self.mode = mode
        let reducer = WishPlacesReducer(userId: userId)
        self._reducer = State(initialValue: reducer)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: DesignTheme.Spacing.lg) {
            HStack {
                Text(mode == .readOnly ? "Wish Places" : "My Wish Places")
                    .font(DesignTheme.Typography.heading)

                Spacer()

                if mode == .editable {
                    Button(action: { reducer.showCreateSheet = true }) {
                        Image(systemName: "plus.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(DesignTheme.accentColor)
                    }
                }
            }

            if reducer.isLoading {
                ProgressView()
                    .frame(maxWidth: .infinity, alignment: .center)
            } else if let errorMessage = reducer.errorMessage {
                VStack(spacing: DesignTheme.Spacing.md) {
                    Text(errorMessage)
                        .font(DesignTheme.Typography.caption)
                        .foregroundColor(DesignTheme.error)
                    Button(action: { reducer.retry() }) {
                        Text("Retry")
                            .font(DesignTheme.Typography.button)
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(DesignTheme.accentColor)
                            .foregroundColor(.white)
                            .cornerRadius(DesignTheme.CornerRadius.capsule)
                    }
                }
            } else if reducer.places.isEmpty {
                Text("No wish places yet")
                    .font(DesignTheme.Typography.caption)
                    .foregroundColor(.gray)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(DesignTheme.Spacing.lg)
            } else {
                List {
                    ForEach(reducer.places, id: \.id) { place in
                        WishPlaceItem(
                            place: place,
                            onTap: {
                                reducer.selectedPlace = place
                            }
                        )
                        .listRowSeparator(.hidden)
                        .listRowBackground(Color.clear)
                        .listRowInsets(EdgeInsets(top: DesignTheme.Spacing.sm, leading: 0, bottom: DesignTheme.Spacing.sm, trailing: 0))
                    }
                    .onDelete { indexSet in
                        for index in indexSet {
                            withAnimation {
                                reducer.deletePlace(id: reducer.places[index].id)
                            }
                        }
                    }
                }
                .listStyle(.plain)
                .scrollDisabled(true)
                .frame(height: CGFloat(reducer.places.count) * 120)
            }

            if reducer.isActionPending {
                HStack(spacing: DesignTheme.Spacing.sm) {
                    ProgressView()
                        .scaleEffect(0.8)
                    Text("Processing...")
                        .font(DesignTheme.Typography.caption)
                        .foregroundColor(.gray)
                }
            }

            Spacer()
        }
        .padding(DesignTheme.Spacing.lg)
        .sheet(isPresented: $reducer.showCreateSheet) {
            CreateWishPlaceSheet(
                onDismiss: {
                    reducer.showCreateSheet = false
                },
                onCreate: { title, description, location, link in
                    reducer.createPlace(
                        title: title,
                        description: description,
                        location: location,
                        link: link
                    )
                }
            )
            .presentationDetents([.medium, .large])
        }
        .sheet(item: $reducer.selectedPlace) { place in
            WishPlaceDetailSheet(place: place)
        }
    }
}

enum WishPlacesMode {
    case editable
    case readOnly
}
