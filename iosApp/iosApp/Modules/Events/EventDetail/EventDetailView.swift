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
    
    init(eventId: String) {
        self.eventId = eventId
        _reducer = State(initialValue: EventDetailReducer(eventId: eventId))
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
                    
                    Text("Participants:")
                        .font(.headline)
                    
                    List(reducer.participants, id: \.userId) { participant in
                        VStack(alignment: .leading, spacing: 4) {
                            Text(participant.username)
                            Text("Role: \(participant.role)")
                            Text("Status: \(participant.responseStatus)")
                        }
                    }
                }
                .padding()
            }
        }
    }
}
