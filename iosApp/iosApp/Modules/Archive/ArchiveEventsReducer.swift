//
//  ArchiveEventsReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 20.05.2026.
//

import SwiftUI
import Shared

@Observable
final class ArchiveEventsReducer {
    
    var archivedEvents: [Event] = []
    var isRefreshing: Bool = false
    var isLoading: Bool = false
    var errorMessage: String?
    
    private let sharedVM: ArchiveEventsViewModel
    private var stateTask: Task<Void, Never>?
    
    init() {
        self.sharedVM = ArchiveEventsViewModel()
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let archiveState = state as? ArchiveViewState else { continue }
                switch archiveState {
                case is ArchiveViewState.Loading:
                    self.isLoading = true
                    self.errorMessage = nil
                case let error as ArchiveViewState.Error:
                    self.errorMessage = error.message
                    self.isLoading = false
                case let content as ArchiveViewState.Content:
                    self.archivedEvents = content.archivedEvents
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
        sharedVM.obtainEvent(event: ArchiveViewAction.OnRefresh())
    }
}
