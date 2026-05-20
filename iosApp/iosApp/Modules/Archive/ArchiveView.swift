//
//  ArchiveView.swift
//  iosApp
//
//  Created by Данил Забинский on 20.05.2026.
//

import SwiftUI
import Shared

struct ArchiveView: View {
    
    @State private var reducer = ArchiveEventsReducer()
    @Environment(Router.self) private var router
    
    var body: some View {
        VStack {
            if reducer.isLoading {
                ProgressView()
                    .frame(maxHeight: .infinity, alignment: .center)
            } else if let errorMessage = reducer.errorMessage {
                VStack(spacing: 12) {
                    Image(systemName: "exclamationmark.circle.fill")
                        .font(.largeTitle)
                        .foregroundColor(.red)
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .multilineTextAlignment(.center)
                    Button(action: {
                        reducer.refresh()
                    }) {
                        Text("Retry")
                            .font(.headline)
                    }
                    .buttonStyle(.bordered)
                }
                .padding()
                .frame(maxHeight: .infinity, alignment: .center)
            } else if reducer.archivedEvents.isEmpty {
                VStack(spacing: 12) {
                    Image(systemName: "archivebox")
                        .font(.largeTitle)
                        .foregroundColor(.gray)
                    Text("No archived events")
                        .foregroundColor(.gray)
                }
                .frame(maxHeight: .infinity, alignment: .center)
            } else {
                ScrollView {
                    VStack(alignment: .leading, spacing: 8) {
                        ForEach(reducer.archivedEvents, id: \.id) { event in
                            EventRowView(event: event, isPending: false)
                                .onTapGesture {
                                    router.push(screen: .eventDetail(id: event.id))
                                }
                                .padding(.horizontal)
                        }
                    }
                    .padding(.vertical)
                }
            }
        }
        .navigationTitle("Archive")
        .refreshable {
            reducer.refresh()
        }
    }
}
