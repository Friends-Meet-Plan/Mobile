//
//  PendingEventDetailSheet.swift
//  iosApp
//
//  Created by Данил Забинский on 18.05.2026.
//

import SwiftUI
import Shared

struct PendingEventDetailSheet: View {
    
    let eventDetail: EventDetail
    let isLoading: Bool
    let error: String?
    let onAccept: () -> Void
    let onDecline: () -> Void
    let onDismiss: () -> Void
    
    @Environment(\.dismiss) var dismiss
    
    private func statusColor(_ status: String) -> Color {
        switch status.lowercased() {
        case "accepted":
            return .green
        case "declined":
            return .red
        case "pending":
            return .orange
        default:
            return .gray
        }
    }
    
    var body: some View {
        NavigationStack {
            if let error = error {
                VStack {
                    Text(error)
                        .foregroundColor(.red)
                        .multilineTextAlignment(.center)
                        .padding()
                }
            } else if isLoading {
                ProgressView()
            } else {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        VStack(alignment: .leading, spacing: 8) {
                            Text(eventDetail.title)
                                .font(.title2)
                                .fontWeight(.bold)
                            
                            if let description = eventDetail.description_ {
                                Text(description)
                                    .font(.body)
                                    .foregroundColor(.secondary)
                            }
                        }
                        
                        Divider()
                        
                        VStack(alignment: .leading, spacing: 12) {
                            Label(eventDetail.date, systemImage: "calendar")
                                .font(.subheadline)
                            
                            if let time = eventDetail.time {
                                Label(time, systemImage: "clock")
                                    .font(.subheadline)
                            }
                            
                            if let location = eventDetail.location {
                                Label(location, systemImage: "location.fill")
                                    .font(.subheadline)
                            }
                            
                            HStack {
                                Text("Status:")
                                    .font(.subheadline)
                                Spacer()
                                Text(eventDetail.status)
                                    .font(.subheadline)
                                    .foregroundColor(statusColor(eventDetail.status))
                                    .fontWeight(.semibold)
                            }
                        }
                        
                        Divider()
                        
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Participants")
                                .font(.headline)
                            
                            VStack(spacing: 12) {
                                ForEach(eventDetail.participants, id: \.userId) { participant in
                                    HStack(spacing: 12) {
                                        VStack(alignment: .leading, spacing: 4) {
                                            Text(participant.username)
                                                .font(.body)
                                                .foregroundStyle(.primary)
                                            Text(participant.role)
                                                .font(.caption)
                                                .foregroundStyle(.gray)
                                        }
                                        
                                        Spacer()
                                        
                                        Text(participant.responseStatus)
                                            .font(.subheadline)
                                            .foregroundColor(statusColor(participant.responseStatus))
                                    }
                                    .padding(.vertical, 4)
                                }
                            }
                        }
                        
                        Spacer()
                            .frame(height: 16)
                        
                        VStack(spacing: 12) {
                            Button(action: onAccept) {
                                Text("Accept Invitation")
                                    .font(.headline)
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.green)
                                    .foregroundColor(.white)
                                    .cornerRadius(8)
                            }
                            .disabled(isLoading)
                            
                            Button(action: onDecline) {
                                Text("Decline Invitation")
                                    .font(.headline)
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.red)
                                    .foregroundColor(.white)
                                    .cornerRadius(8)
                            }
                            .disabled(isLoading)
                        }
                    }
                    .padding()
                }
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button("Close") {
                            dismiss()
                        }
                    }
                }
            }
        }
    }
}
