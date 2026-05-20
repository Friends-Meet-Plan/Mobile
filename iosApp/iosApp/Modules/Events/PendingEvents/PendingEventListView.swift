//
//  PendingEventListView.swift
//  iosApp
//
//  Created by Данил Забинский on 18.05.2026.
//

import SwiftUI
import Shared

struct PendingEventListView: View {
    
    @State private var reducer = PendingEventReducer()
    @Environment(Router.self) private var router
    
    var body: some View {
        VStack {
            if reducer.isLoading {
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let errorMessage = reducer.errorMessage {
                VStack {
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .multilineTextAlignment(.center)
                        .padding()
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                List(reducer.pendingEvents, id: \.id) { event in
                    VStack(alignment: .leading, spacing: 8) {
                        Text(event.title)
                            .font(.headline)
                        
                        HStack(spacing: 12) {
                            Image(systemName: "calendar")
                                .foregroundColor(.gray)
                            Text(event.date)
                                .font(.subheadline)
                                .foregroundColor(.gray)
                        }
                        
                        if let time = event.time {
                            HStack(spacing: 12) {
                                Image(systemName: "clock")
                                    .foregroundColor(.gray)
                                Text(time)
                                    .font(.subheadline)
                                    .foregroundColor(.gray)
                            }
                        }
                        
                        HStack(spacing: 12) {
                            Image(systemName: "person.2")
                                .foregroundColor(.gray)
                            Text("\(event.participants.count) participants")
                                .font(.subheadline)
                                .foregroundColor(.gray)
                        }
                    }
                    .padding(.vertical, 4)
                    .onTapGesture {
                        reducer.fetchEventDetail(eventId: event.id)
                    }
                }
                .refreshable {
                    reducer.refresh()
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Button {
                    router.pop()
                } label: {
                    Image(systemName: "chevron.left")
                        .padding()
                }
            }
        }
        .navigationTitle("Pending Invitations")
        .sheet(item: Binding(
            get: { reducer.selectedEventDetail },
            set: { _ in reducer.closeEventDetail() }
        )) { eventDetail in
            PendingEventDetailSheet(
                eventDetail: eventDetail,
                isLoading: reducer.isLoadingDetail,
                error: reducer.detailError,
                onAccept: { reducer.acceptEvent(eventId: eventDetail.id) },
                onDecline: { reducer.declineEvent(eventId: eventDetail.id) },
                onDismiss: { reducer.closeEventDetail() }
            )
        }
        .toast(isPresented: $reducer.showToast, message: reducer.toastMessage ?? "")
    }
}
