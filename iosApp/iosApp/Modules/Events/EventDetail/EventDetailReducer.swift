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
                    self.id = content.eventDetail.id
                    self.title = content.eventDetail.title
                    self.description = content.eventDetail.description
                    self.date = content.eventDetail.date
                    self.time = content.eventDetail.time
                    self.location = content.eventDetail.location
                    self.creatorId = content.eventDetail.creatorId
                    self.status = content.eventDetail.status
                    self.participants = content.eventDetail.participants
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
