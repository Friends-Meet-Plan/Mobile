//
//  MainView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct MainView: View {
    
    @State private var reducer = MainEventsReducer()
    @State private var isCreatingEventInProgress = false
    @State private var selectedDate = Date()
    @Environment(Router.self) private var router
    
    private let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()
    
    var body: some View {
        VStack {
            if reducer.isLoading {
                ProgressView()
            } else if let errorMessage = reducer.errorMessage {
                Text(errorMessage)
                    .foregroundColor(.red)
            } else {
                Button {
                    router.push(screen: .pendingEvents)
                } label: {
                    Image(systemName: "bell.fill")
                        .padding()
                }
                
                List(reducer.upcomingEvents, id: \.id) { event in
                    VStack(alignment: .leading) {
                        Text(event.title)
                        Text("Date: \(event.date)")
                        if let time = event.time {
                            Text("Time: \(time)")
                        }
                        Text("Participants: \(event.participantCount)")
                    }
                    .onTapGesture {
                        router.push(screen: .eventDetail(id: event.id))
                    }
                }
                .refreshable {
                    reducer.refresh()
                }
            }
            
            Button {
                isCreatingEventInProgress = true
            } label: {
                Text("Create event")
            }
        }
        .navigationTitle("Home")
        .sheet(isPresented: $isCreatingEventInProgress) {
            DatePicker(
                "Select event date",
                selection: $selectedDate,
                in: Date()...,
                displayedComponents: [.date, .hourAndMinute]
            )
            .datePickerStyle(.graphical)
            .padding()
            .presentationDetents([.medium])

            Button("Create") {
                router.push(screen: .createEvent(date: dateFormatter.string(from: selectedDate)))
                isCreatingEventInProgress = false
            }
        }
    }
}
