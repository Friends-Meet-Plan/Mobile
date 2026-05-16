//
//  WishPlacesReducer.swift
//  iosApp
//
//  Created by Данил Забинский on 16.05.2026.
//

import SwiftUI
import Shared

@Observable
final class WishPlacesReducer {
    
    var places: [Shared.WishPlace] = []
    var isLoading = false
    var isActionPending = false
    var errorMessage: String?
    
    var selectedPlace: Shared.WishPlace?
    var showCreateSheet = false
    
    var onPlaceCreated: (() -> Void)?
    var onShowError: ((String) -> Void)?
    
    private let sharedVM = WishPlacesViewModel()
    private var stateTask: Task<Void, Never>?
    private var actionTask: Task<Void, Never>?
    private let userId: String
    
    init(userId: String) {
        self.userId = userId
        let scope = sharedVM.viewModelScope
        
        stateTask = Task {
            for await state in sharedVM.viewStates.asAsyncStream(scope: scope) {
                switch state {
                case is WishPlacesViewState.Loading:
                    self.isLoading = true
                    self.errorMessage = nil
                case let error as WishPlacesViewState.Error:
                    self.isLoading = false
                    self.errorMessage = error.message
                case let content as WishPlacesViewState.Content:
                    self.isLoading = false
                    self.places = content.places
                    self.isActionPending = content.isActionPending
                    self.errorMessage = nil
                default:
                    self.isLoading = false
                    self.isActionPending = false
                }
            }
        }
        
        actionTask = Task {
            let stream = sharedVM.viewActions.asAsyncStream(scope: scope)
            for await rawAction in stream {
                guard let action = rawAction as? WishPlacesAction else { continue }
                if action is WishPlacesAction.PlaceCreated {
                    self.showCreateSheet = false
                    self.onPlaceCreated?()
                } else if let error = action as? WishPlacesAction.ShowError {
                    self.errorMessage = error.message
                    self.onShowError?(error.message)
                }
            }
        }
        
        loadPlaces()
    }
    
    deinit {
        stateTask?.cancel()
        actionTask?.cancel()
        sharedVM.clear()
    }
    
    func loadPlaces() {
        sharedVM.obtainEvent(event: WishPlacesEvent.LoadPlaces(userId: userId))
    }
    
    func createPlace(
        title: String,
        description: String?,
        location: String?,
        link: String?
    ) {
        sharedVM.obtainEvent(
            event: WishPlacesEvent.CreatePlace(
                userId: userId,
                title: title,
                description: description,
                location: location,
                link: link
            )
        )
    }
    
    func deletePlace(id: String) {
        sharedVM.obtainEvent(
            event: WishPlacesEvent.ArchivePlace(
                userId: userId,
                id: id
            )
        )
    }
    
    func retry() {
        loadPlaces()
    }
}
