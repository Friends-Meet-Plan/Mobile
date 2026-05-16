//
//  BusyDaysReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 16.05.2026.
//

import SwiftUI
import Shared

@Observable
final class BusyDaysReducer {
    
    var busyDays: [String] = []
    var isLoading = false
    var errorMessage: String?
    
    private let sharedVM: BusyDaysViewModel
    private var stateTask: Task<Void, Never>?
    
    init(userId: String) {
        self.sharedVM = BusyDaysViewModel(userId: userId)
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let busyDaysState = state as? BusyDaysViewState else { continue }
                switch busyDaysState {
                case is BusyDaysViewState.Loading:
                    self.isLoading = true
                    self.errorMessage = nil
                case let error as BusyDaysViewState.Error:
                    self.isLoading = false
                    self.errorMessage = error.message
                    self.busyDays = []
                case let content as BusyDaysViewState.Content:
                    self.isLoading = false
                    self.errorMessage = nil
                    self.busyDays = content.calendarResponse.busyDays
                default:
                    self.isLoading = false
                    self.busyDays = []
                }
            }
        }
    }
    
    deinit {
        stateTask?.cancel()
        sharedVM.clear()
    }
    
    func retry() {
        sharedVM.obtainEvent(event: BusyDaysEvent.OnRetry())
    }
}
