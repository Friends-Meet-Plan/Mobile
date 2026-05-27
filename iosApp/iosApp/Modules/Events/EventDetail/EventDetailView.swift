//
//  EventDetailView.swift
//  iosApp
//
//  Created by Данил Забинский on 14.05.2026.
//

import SwiftUI
import Shared

struct EventDetailView: View {
    let eventId: String
    
    @State private var reducer: EventDetailReducer
    @Environment(Router.self) private var router
    
    init(eventId: String) {
        self.eventId = eventId
        _reducer = State(initialValue: EventDetailReducer(eventId: eventId))
    }
    
    private func statusColor(_ status: String) -> Color {
        switch status.lowercased() {
        case "accepted":
            return DesignTheme.secondaryAccent
        case "declined":
            return DesignTheme.error
        case "pending":
            return Color.orange
        default:
            return Color.gray
        }
    }
    
    private func statusBadge(_ status: String) -> some View {
        Text(status.capitalized)
            .font(DesignTheme.Typography.bodySmall)
            .foregroundColor(.white)
            .padding(.horizontal, DesignTheme.Spacing.md)
            .padding(.vertical, DesignTheme.Spacing.xs)
            .background(statusColor(status))
            .cornerRadius(DesignTheme.CornerRadius.capsule)
    }
    
    var body: some View {
        if reducer.isLoading {
            LoadingStateView()
        } else if let errorMessage = reducer.errorMessage {
            VStack(spacing: DesignTheme.Spacing.lg) {
                Image(systemName: "exclamationmark.triangle.fill")
                    .font(.system(size: 48))
                    .foregroundColor(DesignTheme.error)
                
                FormErrorMessage(message: errorMessage)
            }
            .padding(DesignTheme.Spacing.lg)
        } else {
            ScrollView {
                VStack(alignment: .leading, spacing: DesignTheme.Spacing.lg) {
                    EventImageView()
                    
                    VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                        HStack(alignment: .top, spacing: DesignTheme.Spacing.md) {
                            VStack(alignment: .leading, spacing: DesignTheme.Spacing.sm) {
                                Text(reducer.title)
                                    .font(DesignTheme.Typography.heading)
                                    .foregroundColor(.black)
                                    .lineLimit(3)
                            }
                            
                            Spacer()
                            
                            statusBadge(reducer.status)
                        }
                    }
                    
                    // Details Section with Icons
                    VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                        DetailRowWithIcon(icon: "calendar", label: "Date", value: reducer.date)
                        
                        if let time = reducer.time {
                            DetailRowWithIcon(icon: "clock", label: "Time", value: time)
                        }
                        
                        if let location = reducer.location {
                            DetailRowWithIcon(icon: "mappin", label: "Location", value: location)
                        }
                    }
                    
                    // Description Section
                    if let description = reducer.description, !description.isEmpty {
                        VStack(alignment: .leading, spacing: DesignTheme.Spacing.sm) {
                            Text("Description")
                                .font(DesignTheme.Typography.button)
                                .foregroundColor(.black)
                            
                            Text(description)
                                .font(DesignTheme.Typography.body)
                                .foregroundColor(.gray)
                                .lineLimit(nil)
                        }
                        .padding(DesignTheme.Spacing.md)
                        .background(Color(.systemGray6))
                        .cornerRadius(DesignTheme.CornerRadius.medium)
                    }
                    
                    VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                        Text("Participants")
                            .font(DesignTheme.Typography.button)
                            .foregroundColor(.black)
                        
                        if reducer.participants.isEmpty {
                            Text("No participants yet")
                                .font(DesignTheme.Typography.bodySmall)
                                .foregroundColor(.secondary)
                                .padding(DesignTheme.Spacing.md)
                        } else {
                            VStack(spacing: DesignTheme.Spacing.md) {
                                ForEach(Array(reducer.participants.enumerated()), id: \.element.userId) { index, participant in
                                    ParticipantAvatarRow(
                                        participant: participant,
                                        statusColor: statusColor,
                                        colorIndex: index
                                    )
                                }
                            }
                        }
                    }
                }
                .padding(DesignTheme.Spacing.lg)
            }
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        router.onCreatedEventPushBack?()
                        router.root()
                    } label: {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(DesignTheme.accentColor)
                            .frame(width: 44, height: 44)
                    }
                }
            }
        }
    }
}

// MARK: - Loading State

private struct LoadingStateView: View {
    var body: some View {
        VStack(spacing: DesignTheme.Spacing.xl) {
            VStack(spacing: DesignTheme.Spacing.lg) {
                // Skeleton event image placeholder
                RoundedRectangle(cornerRadius: DesignTheme.CornerRadius.medium)
                    .fill(Color(.systemGray6))
                    .frame(height: 200)
                    .shimmer()
                
                VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                    // Skeleton title
                    RoundedRectangle(cornerRadius: DesignTheme.CornerRadius.small)
                        .fill(Color(.systemGray6))
                        .frame(height: 24)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .shimmer()
                    
                    // Skeleton subtitle
                    RoundedRectangle(cornerRadius: DesignTheme.CornerRadius.small)
                        .fill(Color(.systemGray6))
                        .frame(height: 16)
                        .frame(maxWidth: 200, alignment: .leading)
                        .shimmer()
                }
                
                VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                    ForEach(0..<3, id: \.self) { _ in
                        RoundedRectangle(cornerRadius: DesignTheme.CornerRadius.small)
                            .fill(Color(.systemGray6))
                            .frame(height: 16)
                            .shimmer()
                    }
                }
                
                Spacer()
            }
            .padding(DesignTheme.Spacing.lg)
            
            VStack(spacing: DesignTheme.Spacing.sm) {
                ProgressView()
                    .tint(DesignTheme.accentColor)
                
                Text("Loading event details...")
                    .font(DesignTheme.Typography.bodySmall)
                    .foregroundColor(.secondary)
            }
        }
        .frame(maxHeight: .infinity)
        .padding(DesignTheme.Spacing.lg)
    }
}

// MARK: - Event Image View

private struct EventImageView: View {
    var body: some View {
        VStack {
            Image("PendingInvitationImage")
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity)
                .padding(.horizontal)
                .clipped()
        }
    }
}

// MARK: - Detail Row with Icon

private struct DetailRowWithIcon: View {
    let icon: String
    let label: String
    let value: String
    
    var body: some View {
        HStack(spacing: DesignTheme.Spacing.md) {
            Image(systemName: icon)
                .font(.system(size: 16, weight: .semibold))
                .foregroundColor(DesignTheme.accentColor)
                .frame(width: 24)
            
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                Text(label)
                    .font(DesignTheme.Typography.bodySmall)
                    .foregroundColor(.secondary)
                
                Text(value)
                    .font(DesignTheme.Typography.body)
                    .foregroundColor(.black)
                    .lineLimit(nil)
            }
            
            Spacer()
        }
        .padding(DesignTheme.Spacing.md)
        .background(Color(.systemGray6))
        .cornerRadius(DesignTheme.CornerRadius.medium)
    }
}

// MARK: - Participant Avatar Row

private struct ParticipantAvatarRow: View {
    let participant: EventParticipant
    let statusColor: (String) -> Color
    let colorIndex: Int
    
    var body: some View {
        HStack(spacing: DesignTheme.Spacing.md) {
            // Avatar with initials and status badge
            ZStack(alignment: .bottomTrailing) {
                // Avatar circle
                Circle()
                    .fill(AvatarColorPalette.color(for: colorIndex))
                    .frame(width: 48, height: 48)
                
                // Initials
                Text(participant.username.prefix(1).uppercased())
                    .font(DesignTheme.Typography.button)
                    .foregroundColor(.white)
                
                statusBadgeIcon(participant.status.description())
                    .frame(width: 20, height: 20)
                    .background(Circle().fill(Color.white))
                    .offset(x: 2, y: 2)
            }
            
            // User info
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                Text(participant.username)
                    .font(DesignTheme.Typography.body)
                    .foregroundColor(.black)
                
                HStack(spacing: DesignTheme.Spacing.sm) {
                    Text(participant.role.capitalized)
                        .font(DesignTheme.Typography.bodySmall)
                        .foregroundColor(.secondary)
                    
                    Circle()
                        .fill(statusColor(participant.status.description()))
                        .frame(width: 6, height: 6)
                    
                    Text(participant.status.description().capitalized)
                        .font(DesignTheme.Typography.bodySmallest)
                        .foregroundColor(statusColor(participant.status.description()))
                }
            }
            
            Spacer()
        }
        .padding(DesignTheme.Spacing.md)
        .background(Color(.systemGray6))
        .cornerRadius(DesignTheme.CornerRadius.medium)
    }
    
    @ViewBuilder
    private func statusBadgeIcon(_ status: String) -> some View {
        switch status.lowercased() {
        case "accepted":
            Image(systemName: "checkmark.circle.fill")
                .font(.system(size: 14, weight: .semibold))
                .foregroundColor(DesignTheme.secondaryAccent)
        case "declined":
            Image(systemName: "xmark.circle.fill")
                .font(.system(size: 14, weight: .semibold))
                .foregroundColor(DesignTheme.error)
        case "pending":
            Image(systemName: "clock.circle.fill")
                .font(.system(size: 14, weight: .semibold))
                .foregroundColor(Color.orange)
        default:
            Image(systemName: "questionmark.circle.fill")
                .font(.system(size: 14, weight: .semibold))
                .foregroundColor(Color.gray)
        }
    }
}

// MARK: - Avatar Color Palette

enum AvatarColorPalette {
    private static let colors: [Color] = [
        Color(red: 0.0, green: 0.48, blue: 1.0),      // Blue
        Color(red: 0.34, green: 0.78, blue: 0.55),    // Green
        Color(red: 1.0, green: 0.58, blue: 0.0),      // Orange
        Color(red: 1.0, green: 0.36, blue: 0.48),     // Pink
        Color(red: 0.67, green: 0.43, blue: 0.97),    // Purple
        Color(red: 0.0, green: 0.78, blue: 0.73),     // Teal
    ]
    
    static func color(for index: Int) -> Color {
        colors[index % colors.count]
    }
}

// MARK: - Shimmer Modifier

extension View {
    func shimmer() -> some View {
        modifier(ShimmerModifier())
    }
}

private struct ShimmerModifier: ViewModifier {
    @State private var isShimmering = false
    
    func body(content: Content) -> some View {
        content
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.white.opacity(0),
                        Color.white.opacity(0.3),
                        Color.white.opacity(0)
                    ]),
                    startPoint: .leading,
                    endPoint: .trailing
                )
                .offset(x: isShimmering ? 400 : -400)
                .animation(.linear(duration: 1.5).repeatForever(autoreverses: false), value: isShimmering)
            )
            .onAppear {
                isShimmering = true
            }
    }
}
