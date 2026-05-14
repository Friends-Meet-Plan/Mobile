//
//  MainEventsReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 14.05.2026.
//

import SwiftUI
import Shared

@Observable
final class MainEventsReducer {
    
    var upcomingEvents: [MainEvent] = []
    var isRefreshing: Bool = false
    
    var isLoading: Bool = false
    var errorMessage: String?

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
                    self.upcomingEvents = content.upcomingEvents
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
        sharedVM.obtainEvent(event: Shared.MainEventOnRefresh())
    }
}
