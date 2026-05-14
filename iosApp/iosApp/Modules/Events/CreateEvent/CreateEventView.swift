//
//  CreateEventView.swift
//  iosApp
//
//  Created by Данил Забинский on 13.05.2026.
//

import SwiftUI
import Shared

struct CreateEventView: View {
    
    @State var reducer: CreateEventReducer
    
    init(date: String) {
        self.reducer = CreateEventReducer(dateString: date)
    }
    
    var body: some View {
        VStack {
            Text(reducer.selectedDate)
            
            if reducer.isLoadingFriends {
                ProgressView()
            }
            
            if let errorMessage = reducer.errorMessage {
                Text(errorMessage)
                    .foregroundStyle(.red)
            }
            
            ScrollView {
                VStack {
                    TextField("Title", text: Binding(
                        get: { reducer.title },
                        set: { reducer.updateTitle($0) }
                    ))
                    
                    TextField("Description", text: Binding(
                        get: { reducer.descriptionText },
                        set: { reducer.updateDescription($0) }
                    ))
                    
                    TextField("Location", text: Binding(
                        get: { reducer.location },
                        set: { reducer.updateLocation($0) }
                    ))
                    
                    Button("Select Friends") {
                        reducer.toggleFriendsSheet()
                    }
                    
                    if !reducer.selectedFriendIds.isEmpty {
                        VStack(alignment: .leading) {
                            Text("Selected Friends: \(reducer.selectedFriendIds.count)")
                            List(reducer.availableFriends.filter { reducer.selectedFriendIds.contains($0.id) }, id: \.id) { friend in
                                Text(friend.username)
                            }
                        }
                    }
                    
                    Button("Create") {
                        reducer.submit()
                    }
                    .disabled(!reducer.isCreateButtonEnabled)
                    
                    Button("Back") {
                        reducer.back()
                    }
                }
            }
            
            if reducer.isCreatingEvent {
                ProgressView()
            }
        }
        .sheet(isPresented: Binding(
            get: { reducer.showFriendsSheet },
            set: { reducer.showFriendsSheet = $0 }
        )) {
            NavigationStack {
                VStack {
                    if reducer.isLoadingFriends {
                        ProgressView()
                    } else if let error = reducer.friendsError {
                        Text(error)
                            .foregroundStyle(.red)
                    } else if reducer.availableFriends.isEmpty {
                        Text("No friends available")
                    } else {
                        List(reducer.availableFriends, id: \.id) { friend in
                            Button(action: {
                                reducer.toggleFriend(friend.id)
                            }) {
                                HStack {
                                    Text(friend.username)
                                    Spacer()
                                    if reducer.selectedFriendIds.contains(friend.id) {
                                        Text("✓")
                                    }
                                }
                            }
                        }
                    }
                }
                .navigationTitle("Select Friends")
                .toolbar {
                    ToolbarItem(placement: .topBarTrailing) {
                        Button("Done") {
                            reducer.showFriendsSheet = false
                        }
                    }
                }
            }
        }
    }
}
