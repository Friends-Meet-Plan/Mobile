//
//  EventsReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 14.05.2026.
//

import SwiftUI
import Shared

enum EventFilter: Int, CaseIterable {
    case active = 0
    case pending = 1
    
    var title: String {
        switch self {
        case .active:
            return "Active"
        case .pending:
            return "Pending"
        }
    }
}

@Observable
final class EventsReducer {

    var activeEvents: [Event] = []
    var pendingEvents: [Event] = []
    var isRefreshing: Bool = false

    var isLoading: Bool = false
    var errorMessage: String?

    var selectedFilter: EventFilter = .active
    var isCheckingAvailability: Bool = false

    private let sharedVM: MainViewModel
    private var stateTask: Task<Void, Never>?
    
    init() {
        self.sharedVM = MainViewModel()
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let mainState = state as? MainViewState else { continue }
                switch mainState {
                case is MainViewState.Loading:
                    self.isLoading = true
                    self.errorMessage = nil
                case let error as MainViewState.Error:
                    self.errorMessage = error.message
                    self.isLoading = false
                case let content as MainViewState.Content:
                    self.activeEvents = content.activeEvents
                    self.pendingEvents = content.pendingEvents
                    self.isRefreshing = content.isRefreshing
                    self.isLoading = false
                    self.errorMessage = nil
                default:
                    self.errorMessage = nil
                    self.isLoading = false
                }
            }
        }
    }
    
    deinit {
        stateTask?.cancel()
        sharedVM.clear()
    }
    
    func refresh() {
        sharedVM.obtainEvent(event: MainViewAction.OnRefresh())
    }

    func checkAvailability(date: String) async -> Bool {
        isCheckingAvailability = true
        defer { isCheckingAvailability = false }
        do {
            let result = try await sharedVM.checkAvailability(date: date)
            guard let availability = result else { return true }
            
            switch availability {
            case let isAvailable as Bool:
                return isAvailable
            default:
                return false
            }
        } catch {
            self.errorMessage = error.localizedDescription
            return false
        }
    }

    var filteredEvents: [Event] {
        switch selectedFilter {
        case .active:
            return activeEvents
        case .pending:
            return pendingEvents
        }
    }

    var pendingCount: Int {
        pendingEvents.count
    }
}
