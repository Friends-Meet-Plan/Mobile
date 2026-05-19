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
    @State private var showBusyAlert = false
    @State private var selectedDateForEvent: String?
    @Environment(Router.self) private var router

    private let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()

    var body: some View {
        VStack {
            // Header with Bell Button and Badge
            HStack {
                Spacer()
                Button {
                    router.push(screen: .pendingEvents)
                } label: {
                    ZStack(alignment: .topTrailing) {
                        Image(systemName: "bell.fill")
                            .font(.title2)

                        if reducer.pendingCount > 0 {
                            Text("\(reducer.pendingCount)")
                                .font(.caption2)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                                .frame(width: 20, height: 20)
                                .background(Color.red)
                                .clipShape(Circle())
                                .offset(x: 8, y: -8)
                        }
                    }
                }
                .padding()
            }

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
            } else if reducer.activeEvents.isEmpty && reducer.pendingEvents.isEmpty {
                // Empty state: no events at all
                VStack(spacing: 12) {
                    Image(systemName: "calendar")
                        .font(.largeTitle)
                        .foregroundColor(.gray)
                    Text("You have no events, create it now!")
                        .foregroundColor(.gray)
                }
                .frame(maxHeight: .infinity, alignment: .center)
            } else {
                // Events List with sections
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        // Active Events Section
                        if !reducer.activeEvents.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Active")
                                    .font(.headline)
                                    .fontWeight(.semibold)
                                    .padding(.horizontal)

                                VStack(spacing: 0) {
                                    ForEach(reducer.activeEvents, id: \.id) { event in
                                        EventRowView(event: event, isPending: false)
                                            .onTapGesture {
                                                router.push(screen: .eventDetail(id: event.id))
                                            }
                                            .padding(.horizontal)
                                    }
                                }
                            }
                        }

                        // Pending Events Section
                        if !reducer.pendingEvents.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Pending")
                                    .font(.headline)
                                    .fontWeight(.semibold)
                                    .padding(.horizontal)

                                VStack(spacing: 0) {
                                    ForEach(reducer.pendingEvents, id: \.id) { event in
                                        EventRowView(event: event, isPending: true)
                                            .onTapGesture {
                                                router.push(screen: .eventDetail(id: event.id))
                                            }
                                            .padding(.horizontal)
                                    }
                                }
                            }
                        }
                    }
                    .padding(.vertical)
                }
            }

            Button(action: {
                isCreatingEventInProgress = true
            }) {
                Text("Create event")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .padding()
            .refreshable {
                reducer.refresh()
            }
        }
        .navigationTitle("Home")
        .sheet(isPresented: $isCreatingEventInProgress) {
            VStack(spacing: 16) {
                DatePicker(
                    "Select event date",
                    selection: $selectedDate,
                    in: Date()...,
                    displayedComponents: [.date, .hourAndMinute]
                )
                .datePickerStyle(.graphical)
                .padding()

                Button("Create") {
                    let dateString = dateFormatter.string(from: selectedDate)
                    Task {
                        let isAvailable = await reducer.checkAvailability(date: dateString)
                        if isAvailable {
                            router.push(screen: .createEvent(date: dateString))
                            isCreatingEventInProgress = false
                        } else {
                            selectedDateForEvent = dateString
                            showBusyAlert = true
                        }
                    }
                }
                .disabled(reducer.isCheckingAvailability)
            }
            .presentationDetents([.medium])
        }
        .alert("You are busy on this day", isPresented: $showBusyAlert) {
            Button("OK") {
                showBusyAlert = false
            }
        }
    }
}

// MARK: - Event Row Component
struct EventRowView: View {
    let event: MainEvent
    let isPending: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(event.title)
                    .font(.headline)
                    .fontWeight(.semibold)

                if isPending {
                    Text("PENDING")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 2)
                        .background(Color.orange)
                        .cornerRadius(4)
                }

                Spacer()
            }

            HStack(spacing: 16) {
                HStack(spacing: 4) {
                    Image(systemName: "calendar")
                        .foregroundColor(.gray)
                    Text(event.date)
                        .font(.caption)
                        .foregroundColor(.gray)
                }

                if let time = event.time {
                    HStack(spacing: 4) {
                        Image(systemName: "clock")
                            .foregroundColor(.gray)
                        Text(time)
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                }

                Spacer()
            }

            HStack(spacing: 4) {
                Image(systemName: "person.2")
                    .foregroundColor(.gray)
                Text("\(event.participantCount) participants")
                    .font(.caption)
                    .foregroundColor(.gray)
            }
        }
        .padding(.vertical, 4)
        .listRowBackground(
            isPending ?
            Color(red: 1.0, green: 0.97, blue: 0.92).opacity(0.6) :
            Color.clear
        )
    }
}
