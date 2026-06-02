//
//  MainView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct MainView: View {

    @State private var reducer: EventsReducer!
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
        Group {
            if reducer == nil {
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ZStack(alignment: .bottomTrailing) {

                    backgroundLayer

                    VStack(spacing: 0) {

                        headerView

                        contentView
                    }

                    floatingCreateButton
                }
                .sheet(isPresented: $isCreatingEventInProgress) {
                    createEventSheet
                }
                .alert("You are busy on this day", isPresented: $showBusyAlert) {
                    Button("OK") {
                        showBusyAlert = false
                    }
                }
                .task {
                    router.onCreatedEventPushBack = {
                        reducer.refresh()
                    }
                }
                .navigationBarTitleDisplayMode(.inline)
            }
        }
        .onAppear {
            if reducer == nil {
                reducer = EventsReducer()
            }
        }
    }
}

// MARK: - Main Layout

private extension MainView {
    
    var backgroundLayer: some View {
        Color(.systemGroupedBackground)
            .ignoresSafeArea()
    }
    
    var headerView: some View {
        VStack(alignment: .leading, spacing: 12) {
            
            HStack {
                
                VStack(alignment: .leading, spacing: 4) {
                    Text("Your Events")
                        .font(.system(size: 30, weight: .bold))
                    
                    Text("Manage active and upcoming plans")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                
                Spacer()
                
                Button {
                    router.push(screen: .pendingEvents)
                } label: {
                    ZStack {
                        Circle()
                            .fill(.ultraThinMaterial)
                            .frame(width: 46, height: 46)
                        
                        Image(systemName: "bell.fill")
                            .font(.title3)
                            .foregroundColor(DesignTheme.accentColor)
                    }
                }
            }
        }
        .padding(.horizontal, DesignTheme.Spacing.lg)
        .padding(.top, DesignTheme.Spacing.lg)
        .padding(.bottom, DesignTheme.Spacing.md)
        .background(
            LinearGradient(
                colors: [
                    DesignTheme.accentColor.opacity(0.12),
                    Color(.systemGroupedBackground)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
        )
    }
    
    @ViewBuilder
    var contentView: some View {

        if reducer.isLoading {

            ProgressView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)

        } else if let errorMessage = reducer.errorMessage {

            errorState(message: errorMessage)

        } else if reducer.activeEvents.isEmpty && reducer.pendingEvents.isEmpty {

            emptyState

        } else {
            
            ScrollView {
                VStack(spacing: DesignTheme.Spacing.xl) {
                    
                    if !reducer.activeEvents.isEmpty {
                        sectionView(
                            title: "Active",
                            count: reducer.activeEvents.count,
                            tint: DesignTheme.secondaryAccent
                        ) {
                            ForEach(reducer.activeEvents, id: \.id) { event in
                                EventRowView(
                                    event: event,
                                    isPending: false
                                )
                                .onTapGesture {
                                    router.push(
                                        screen: .eventDetail(id: event.id)
                                    )
                                }
                            }
                        }
                    }
                    
                    if !reducer.pendingEvents.isEmpty {
                        sectionView(
                            title: "Pending",
                            count: reducer.pendingEvents.count,
                            tint: .orange
                        ) {
                            ForEach(reducer.pendingEvents, id: \.id) { event in
                                EventRowView(
                                    event: event,
                                    isPending: true
                                )
                                .onTapGesture {
                                    router.push(
                                        screen: .eventDetail(id: event.id)
                                    )
                                }
                            }
                        }
                    }
                }
                .padding(.horizontal, DesignTheme.Spacing.lg)
                .padding(.top, DesignTheme.Spacing.md)
                .padding(.bottom, 100)
            }
            .refreshable {
                await withCheckedContinuation { continuation in
                    let _ = Task {
                        while reducer.isRefreshing {
                            try? await Task.sleep(
                                nanoseconds: 100_000_000
                            )
                        }
                        continuation.resume()
                    }
                    
                    reducer.refresh()
                }
            }
        }
    }
    
    var floatingCreateButton: some View {
        Button {
            isCreatingEventInProgress = true
        } label: {
            Image(systemName: "plus")
                .font(.title2.weight(.bold))
                .foregroundColor(.white)
                .frame(width: 58, height: 58)
                .background(
                    Circle()
                        .fill(DesignTheme.accentColor)
                        .shadow(
                            color: .primary.opacity(0.15),
                            radius: 10,
                            x: 0,
                            y: 6
                        )
                )
        }
        .padding(.trailing, DesignTheme.Spacing.lg)
        .padding(.bottom, DesignTheme.Spacing.lg)
    }
    
    var createEventSheet: some View {
        VStack(spacing: DesignTheme.Spacing.xs) {

            Text("Create new event")
                .font(.title2.weight(.bold))

            DatePicker(
                "Select event date",
                selection: $selectedDate,
                in: Date()...,
                displayedComponents: [.date, .hourAndMinute]
            )
            .datePickerStyle(.graphical)

            ButtonFactory.primaryLoading(
                action: {
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
                },
                label: "Create",
                isLoading: reducer.isCheckingAvailability,
                isEnabled: !reducer.isCheckingAvailability
            )
        }
        .padding(.horizontal)
        .presentationDetents([.height(UIScreen.main.bounds.height * 0.6)])
        .presentationCornerRadius(24)
    }
    
    func errorState(message: String) -> some View {
        VStack(spacing: DesignTheme.Spacing.lg) {

            Image(systemName: "exclamationmark.circle.fill")
                .font(.system(size: 48))
                .foregroundColor(DesignTheme.error)

            Text(message)
                .multilineTextAlignment(.center)
                .foregroundColor(DesignTheme.error)

            ButtonFactory.primary(
                action: { reducer.refresh() },
                label: "Retry"
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding()
    }
    
    var emptyState: some View {
        VStack(spacing: DesignTheme.Spacing.lg) {
            
            Image(systemName: "calendar.badge.plus")
                .font(.system(size: 52))
                .foregroundColor(.gray)
            
            Text("No events yet")
                .font(.headline)
            
            Text("Tap + to create your first event")
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
    
    func sectionView<Content: View>(
        title: String,
        count: Int,
        tint: Color,
        @ViewBuilder content: () -> Content
    ) -> some View {
        
        VStack(alignment: .leading, spacing: 12) {
            
            HStack {
                
                Text(title)
                    .font(.headline)
                
                Spacer()
                
                Text("\(count)")
                    .font(.caption.weight(.semibold))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 5)
                    .background(
                        Capsule()
                            .fill(tint.opacity(0.12))
                    )
                    .foregroundColor(tint)
            }
            
            content()
        }
    }
}

// MARK: - Event Row

struct EventRowView: View {
    
    let event: Event
    let isPending: Bool
    let forceHideBadge: Bool
    
    init(event: Event, isPending: Bool, forceHideBadge: Bool = false) {
        self.event = event
        self.isPending = isPending
        self.forceHideBadge = forceHideBadge
    }
    
    private var tint: Color {
        isPending ? .orange : DesignTheme.secondaryAccent
    }
    
    private var badgeIcon: String {
        isPending
        ? "clock.fill"
        : "checkmark.circle.fill"
    }
    
    private var badgeText: String {
        isPending
        ? "Pending"
        : "Active"
    }
    
    var body: some View {
        
        VStack(
            alignment: .leading,
            spacing: DesignTheme.Spacing.sm
        ) {
            
            HStack {
                
                Text(event.title)
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundColor(.primary)
                
                Spacer()
                
                if !forceHideBadge {
                    badgeView
                }
            }
            
            HStack(spacing: 8) {
                
                infoChip(
                    icon: "calendar",
                    text: event.date
                )
                
                if let time = event.time {
                    infoChip(
                        icon: "clock",
                        text: time
                    )
                }
                
                infoChip(
                    icon: "person.2",
                    text: "\(event.participants.count)"
                )
                
                Spacer()
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 18)
                .fill(Color(.systemBackground))
                .shadow(
                    color: .primary.opacity(0.06),
                    radius: 8,
                    x: 0,
                    y: 4
                )
        )
        .overlay(
            RoundedRectangle(cornerRadius: 18)
                .stroke(
                    tint.opacity(0.15),
                    lineWidth: 1
                )
        )
    }
    
    private var badgeView: some View {
        HStack(spacing: 4) {
            
            Image(systemName: badgeIcon)
                .font(.caption)
            
            Text(badgeText)
                .font(.caption.weight(.semibold))
        }
        .foregroundColor(tint)
        .padding(.horizontal, 8)
        .padding(.vertical, 5)
        .background(
            Capsule()
                .fill(tint.opacity(0.12))
        )
    }
    
    private func infoChip(
        icon: String,
        text: String
    ) -> some View {
        
        HStack(spacing: 4) {
            
            Image(systemName: icon)
            
            Text(text)
        }
        .font(.caption)
        .foregroundStyle(.secondary)
        .padding(.horizontal, 8)
        .padding(.vertical, 6)
        .background(
            Capsule()
                .fill(Color(.secondarySystemBackground))
        )
    }
}
