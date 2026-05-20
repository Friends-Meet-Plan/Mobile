//
//  PendingEventReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 18.05.2026.
//

import SwiftUI
import Shared

@Observable
final class PendingEventReducer {
    
    var pendingEvents: [Event] = []
    var selectedEventDetail: EventDetail?
    var isLoading: Bool = false
    var isRefreshing: Bool = false
    var detailError: String?
    var isLoadingDetail: Bool = false
    var toastMessage: String?
    var showToast: Bool = false
    
    private let sharedVM: PendingEventViewModel
    private var stateTask: Task<Void, Never>?
    private var actionTask: Task<Void, Never>?
    
    init() {
        self.sharedVM = PendingEventViewModel()
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let pendingState = state as? PendingViewState else { continue }
                switch pendingState {
                case is PendingViewState.Loading:
                    self.isLoading = true
                    self.errorMessage = nil
                case let error as PendingViewState.Error:
                    self.errorMessage = error.message
                    self.isLoading = false
                case let content as PendingViewState.Content:
                    self.pendingEvents = content.events
                    self.isRefreshing = content.isRefreshing
                    self.selectedEventDetail = content.selectedEventDetail
                    self.isLoadingDetail = content.isLoadingDetail
                    self.detailError = content.detailError
                    self.isLoading = false
                    self.errorMessage = nil
                default:
                    self.errorMessage = nil
                    self.isLoading = false
                }
            }
        }
        
        actionTask = Task {
            for await action in sharedVM.viewActions.asAsyncStream(scope: scope) {
                guard let pendingAction = action as? PendingAction else { continue }
                switch pendingAction {
                case let error as PendingAction.ShowMessage:
                    self.toastMessage = error.message
                    self.showToast = true
                    try? await Task.sleep(nanoseconds: 2_000_000_000)
                    self.showToast = false
                default:
                    break
                }
            }
        }
    }
    
    var errorMessage: String?
    
    deinit {
        stateTask?.cancel()
        actionTask?.cancel()
        sharedVM.clear()
    }
    
    func refresh() {
        sharedVM.obtainEvent(event: PendingEvent.OnRefresh())
    }
    
    func fetchEventDetail(eventId: String) {
        sharedVM.obtainEvent(event: PendingEvent.OnEventClick(eventId: eventId))
    }
    
    func acceptEvent(eventId: String) {
        sharedVM.obtainEvent(event: PendingEvent.OnAcceptEvent(eventId: eventId))
    }
    
    func declineEvent(eventId: String) {
        sharedVM.obtainEvent(event: PendingEvent.OnDeclineEvent(eventId: eventId))
    }
    
    func closeEventDetail() {
        sharedVM.closeEventDetail()
    }
}
