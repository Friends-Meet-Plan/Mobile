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
        VStack(spacing: .zero) {
            if reducer.isLoading {
                VStack(spacing: DesignTheme.Spacing.lg) {
                    ProgressView()
                        .scaleEffect(1.5)
                    Text("Loading invitations...")
                        .font(DesignTheme.Typography.body)
                        .foregroundColor(.gray)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color(.systemBackground))
            } else if let errorMessage = reducer.errorMessage {
                VStack(spacing: DesignTheme.Spacing.lg) {
                    ErrorBanner(message: errorMessage)

                    ButtonFactory.primary(
                        action: { reducer.refresh() },
                        label: "Retry"
                    )
                }
                .padding(DesignTheme.Spacing.xl)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
                .background(Color(.systemBackground))
            } else if reducer.pendingEvents.isEmpty {
                emptyStateView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color(.systemBackground))
            } else {
                List(reducer.pendingEvents, id: \.id) { event in
                    eventListItem(event: event)
                        .listRowSeparator(.hidden)
                        .listRowBackground(Color.clear)
                        .listRowInsets(EdgeInsets(
                            top: DesignTheme.Spacing.sm,
                            leading: DesignTheme.Spacing.lg,
                            bottom: DesignTheme.Spacing.sm,
                            trailing: DesignTheme.Spacing.lg
                        ))
                        .onTapGesture {
                            reducer.fetchEventDetail(eventId: event.id)
                        }
                }
                .listStyle(.plain)
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
                        .foregroundColor(DesignTheme.accentColor)
                        .font(.system(size: 16, weight: .semibold))
                }
                .padding(DesignTheme.Spacing.md)
            }
        }
        .navigationTitle("Pending Invitations")
        .navigationBarTitleDisplayMode(.inline)
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
    }
    
    @ViewBuilder
    private func eventListItem(event: Event) -> some View {
        VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
            HStack(spacing: DesignTheme.Spacing.md) {
                VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                    Text(event.title)
                        .font(DesignTheme.Typography.captionSemibold)
                        .foregroundColor(.black)
                        .lineLimit(1)
                    
                    Text(event.date)
                        .font(DesignTheme.Typography.bodySmallest)
                        .foregroundColor(.gray)
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: DesignTheme.Spacing.xs) {
                    HStack(spacing: DesignTheme.Spacing.xs) {
                        Image(systemName: "person.2.fill")
                            .font(.system(size: 12))
                            .foregroundColor(DesignTheme.accentColor)
                        
                        Text("\(event.participants.count)")
                            .font(DesignTheme.Typography.bodySmallest)
                            .foregroundColor(.gray)
                    }
                    
                    if let time = event.time {
                        Text(time)
                            .font(DesignTheme.Typography.bodySmallest)
                            .foregroundColor(.gray)
                    }
                }
            }
            
            Divider()
                .padding(.vertical, DesignTheme.Spacing.xs)
            
            HStack(spacing: DesignTheme.Spacing.sm) {
                Image(systemName: "bell.fill")
                    .font(.system(size: 12))
                    .foregroundColor(DesignTheme.secondaryAccent)
                
                Text("Awaiting your response")
                    .font(DesignTheme.Typography.bodySmallest)
                    .foregroundColor(DesignTheme.secondaryAccent)
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundColor(.gray)
            }
        }
        .padding(DesignTheme.Spacing.lg)
        .background(Color(.systemGray6))
        .cornerRadius(DesignTheme.CornerRadius.medium)
    }
    
    @ViewBuilder
    private func emptyStateView() -> some View {
        VStack(spacing: DesignTheme.Spacing.lg) {
            Image(systemName: "envelope.open.fill")
                .font(.system(size: 48))
                .foregroundColor(DesignTheme.accentColor.opacity(0.5))
            
            VStack(spacing: DesignTheme.Spacing.xs) {
                Text("No Pending Invitations")
                    .font(DesignTheme.Typography.captionSemibold)
                    .foregroundColor(.black)
                
                Text("You're all caught up!")
                    .font(DesignTheme.Typography.bodySmall)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
            }
            
            Spacer()
        }
        .padding(DesignTheme.Spacing.xl)
    }
}
