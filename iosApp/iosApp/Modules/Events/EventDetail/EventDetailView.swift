//
//  EventDetailView.swift
//  iosApp
//
//  Created by Данил Забинский on 14.05.2026.
//

import SwiftUI
import Shared

struct EventDetailView: View {
    let eventId: String
    
    @State private var reducer: EventDetailReducer
    @Environment(Router.self) private var router
    
    init(eventId: String) {
        self.eventId = eventId
        _reducer = State(initialValue: EventDetailReducer(eventId: eventId))
    }
    
    private func statusColor(_ status: String) -> Color {
        switch status.lowercased() {
        case "accepted":
            return .green
        case "declined":
            return .red
        case "pending":
            return .orange
        default:
            return .gray
        }
    }
    
    var body: some View {
        if reducer.isLoading {
            ProgressView()
        } else if let errorMessage = reducer.errorMessage {
            Text(errorMessage)
                .foregroundColor(.red)
        } else {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text(reducer.title)
                    Text("Date: \(reducer.date)")
                    if let time = reducer.time {
                        Text("Time: \(time)")
                    }
                    if let location = reducer.location {
                        Text("Location: \(location)")
                    }
                    Text("Status: \(reducer.status)")
                    
                    if let description = reducer.description {
                        Text("Description: \(description)")
                    }
                    
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Participants")
                            .font(.headline)
                        
                        List(reducer.participants, id: \.userId) { participant in
                            HStack(spacing: 12) {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(participant.username)
                                        .font(.body)
                                        .foregroundStyle(.black)
                                    Text(participant.role)
                                        .font(.caption)
                                        .foregroundStyle(.gray)
                                }
                                
                                Spacer()
                                
                                Text(participant.responseStatus)
                                    .font(.subheadline)
                                    .foregroundColor(statusColor(participant.responseStatus))
                            }
                            .listRowInsets(EdgeInsets())
                        }
                        .listStyle(.plain)
                        .scrollDisabled(true)
                        .frame(height: CGFloat(reducer.participants.count) * 60)
                    }
                }
                .padding()
            }
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        router.root()
                    } label: {
                        Image(systemName: "chevron.left")
                            .padding()
                    }
                }
            }
        }
    }
}
