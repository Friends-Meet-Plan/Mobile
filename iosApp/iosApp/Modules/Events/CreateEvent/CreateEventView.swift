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
    @Environment(Router.self) private var router
    
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
                        get: { reducer.description },
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
                }
            }
            
            if reducer.isCreatingEvent {
                ProgressView()
            }
        }
        .task {
            reducer.onNavigateToEventDetail = { eventId in
                router.path = NavigationPath([AppRouter.eventDetail(id: eventId)])
            }
        }
        .sheet(isPresented: Binding(
            get: { reducer.showFriendsSheet },
            set: { reducer.showFriendsSheet = $0 }
        )) {
            VStack {
                if reducer.isLoadingFriends {
                    ProgressView()
                } else if let error = reducer.friendsError {
                    Text(error)
                        .foregroundStyle(.red)
                } else if reducer.availableFriends.isEmpty {
                    Text("No friends available on selected date")
                } else {
                    List(reducer.availableFriends, id: \.id) { friend in
                        Button {
                            reducer.toggleFriend(friend.id)
                        } label: {
                            HStack {
                                Text(friend.username)
                                    .background {
                                        Color.blue
                                            .opacity(reducer.selectedFriendIds.contains(friend.id) ? 1 : 0)
                                    }
                                Spacer()
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
