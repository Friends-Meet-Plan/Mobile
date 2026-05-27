//
//  EventDetailReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 14.05.2026.
//

import SwiftUI
import Shared

@Observable
final class EventDetailReducer {
    
    var id: String = ""
    var title: String = ""
    var description: String?
    var date: String = ""
    var time: String?
    var location: String?
    var creatorId: String = ""
    var status: String = ""
    var participants: [EventParticipant] = []
    
    var isLoading: Bool = false
    var errorMessage: String?
    
    private let sharedVM: EventDetailViewModel
    private var stateTask: Task<Void, Never>?
    
    init(eventId: String) {
        self.sharedVM = EventDetailViewModel(eventId: eventId)
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                guard let eventState = state as? EventDetailViewState else { continue }
                switch eventState {
                case is EventDetailViewState.Loading:
                    self.isLoading = true
                    self.errorMessage = nil
                case let error as EventDetailViewState.Error:
                    self.errorMessage = error.message
                    self.isLoading = false
                case let content as EventDetailViewState.Content:
                    self.id = content.event.id
                    self.title = content.event.title
                    self.description = content.event.description_
                    self.date = content.event.date
                    self.time = content.event.time
                    self.location = content.event.location
                    self.creatorId = content.event.creatorId
                    self.status = content.event.status
                    self.participants = content.event.participants
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
}
