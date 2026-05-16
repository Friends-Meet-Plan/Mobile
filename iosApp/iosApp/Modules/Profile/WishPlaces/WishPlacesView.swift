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
        VStack(alignment: .leading, spacing: 16) {
            Text(mode == .readOnly ? "Wish Places" : "My Wish Places")
                .font(.headline)
                .fontWeight(.semibold)
            
            if mode == .editable {
                Button {
                    reducer.showCreateSheet = true
                } label: {
                    HStack {
                        Image(systemName: "plus.circle.fill")
                        Text("Create Wish Place")
                    }
                }
            }
            
            ZStack {
                if reducer.isLoading {
                    ProgressView()
                } else if let errorMessage = reducer.errorMessage {
                    VStack(spacing: 12) {
                        Text(errorMessage)
                            .foregroundColor(.red)
                        Button("Retry") {
                            reducer.retry()
                        }
                        .tint(.blue)
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                } else if reducer.places.isEmpty {
                    Text("No wish places yet")
                        .foregroundColor(.gray)
                        .padding()
                } else {
                    LazyVStack {
                        ForEach(reducer.places, id: \.id) { place in
                            WishPlaceItem(
                                place: place,
                                canDelete: mode == .editable,
                                onTap: {
                                    reducer.selectedPlace = place
                                },
                                onDelete: {
                                    withAnimation {
                                        reducer.deletePlace(id: place.id)
                                    }
                                }
                            )
                            .listRowSeparator(.hidden)
                            .listRowBackground(Color.clear)
                            .listStyle(.plain)
                        }
                        .onDelete { indexSet in
                            reducer.places.remove(atOffsets: indexSet)
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            
            if reducer.isActionPending {
                HStack(spacing: 8) {
                    ProgressView()
                        .scaleEffect(0.8)
                    Text("Processing...")
                        .font(.caption)
                        .foregroundColor(.gray)
                }
            }
        }
        .padding()
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
