//
//  PendingEventDetailSheet.swift
//  iosApp
//
//  Created by Данил Забинский on 18.05.2026.
//

import SwiftUI
import Shared

struct PendingEventDetailSheet: View {

    let eventDetail: Event
    let isLoading: Bool
    let error: String?
    let onAccept: () -> Void
    let onDecline: () -> Void
    let onDismiss: () -> Void

    @Environment(\.dismiss) var dismiss

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

    var body: some View {
        NavigationStack {
            if let error = error {
                ErrorState(message: error)
            } else if isLoading {
                LoadingState()
            } else {
                ContentView(
                    eventDetail: eventDetail,
                    statusColor: statusColor,
                    onAccept: onAccept,
                    onDecline: onDecline
                )
            }
        }
    }

    private struct ErrorState: View {
        let message: String

        var body: some View {
            VStack(alignment: .center, spacing: DesignTheme.Spacing.lg) {
                Image(systemName: "exclamationmark.triangle.fill")
                    .font(.system(size: 48))
                    .foregroundColor(DesignTheme.error)

                Text("Something went wrong")
                    .font(DesignTheme.Typography.heading)
                    .multilineTextAlignment(.center)

                Text(message)
                    .font(DesignTheme.Typography.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }
            .padding(DesignTheme.Spacing.lg)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
        }
    }

    private struct LoadingState: View {
        var body: some View {
            VStack(alignment: .center, spacing: DesignTheme.Spacing.lg) {
                ProgressView()
                    .scaleEffect(1.5)

                Text("Loading event details...")
                    .font(DesignTheme.Typography.body)
                    .foregroundColor(.secondary)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
        }
    }

    private struct ContentView: View {
        let eventDetail: Event
        let statusColor: (String) -> Color
        let onAccept: () -> Void
        let onDecline: () -> Void

        @Environment(\.dismiss) var dismiss

        var body: some View {
            ScrollView {
                VStack(alignment: .leading, spacing: DesignTheme.Spacing.xl) {
                    HeaderSection(
                        title: eventDetail.title,
                        description: eventDetail.description_,
                        status: eventDetail.status,
                        statusColor: statusColor
                    )

                    EventDetailsSection(
                        date: eventDetail.date,
                        time: eventDetail.time,
                        location: eventDetail.location
                    )

                    ParticipantsSection(
                        participants: eventDetail.participants,
                        statusColor: statusColor
                    )

                    ActionsSection(
                        onAccept: onAccept,
                        onDecline: onDecline
                    )

                    Spacer()
                        .frame(height: DesignTheme.Spacing.md)
                }
                .padding(DesignTheme.Spacing.lg)
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: { dismiss() }) {
                        HStack(spacing: DesignTheme.Spacing.xs) {
                            Image(systemName: "chevron.left")
                                .fontWeight(.semibold)
                            Text("Back")
                        }
                        .foregroundColor(DesignTheme.accentColor)
                    }
                }
            }
        }
    }

    private struct HeaderSection: View {
        let title: String
        let description: String?
        let status: String
        let statusColor: (String) -> Color

        var body: some View {
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                Text(title)
                    .font(DesignTheme.Typography.heading)
                    .lineLimit(nil)

                if let description = description {
                    Text(description)
                        .font(DesignTheme.Typography.body)
                        .foregroundColor(.secondary)
                        .lineLimit(nil)
                }

                HStack(spacing: DesignTheme.Spacing.sm) {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 12))
                        .foregroundColor(statusColor(status))

                    Text(status.capitalized)
                        .font(DesignTheme.Typography.captionSemibold)
                        .foregroundColor(statusColor(status))

                    Spacer()
                }
                .padding(.vertical, DesignTheme.Spacing.sm)
                .padding(.horizontal, DesignTheme.Spacing.md)
                .background(statusColor(status).opacity(0.08))
                .cornerRadius(DesignTheme.CornerRadius.medium)
            }
        }
    }

    private struct EventDetailsSection: View {
        let date: String
        let time: String?
        let location: String?

        var body: some View {
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                Text("Event Details")
                    .font(DesignTheme.Typography.captionSemibold)
                    .foregroundColor(.secondary)
                    .textCase(.uppercase)

                VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                    DetailRow(
                        icon: "calendar",
                        label: "Date",
                        value: date
                    )

                    if let time = time {
                        DetailRow(
                            icon: "clock",
                            label: "Time",
                            value: time
                        )
                    }

                    if let location = location {
                        DetailRow(
                            icon: "location.fill",
                            label: "Location",
                            value: location
                        )
                    }
                }
            }
        }
    }

    private struct DetailRow: View {
        let icon: String
        let label: String
        let value: String

        var body: some View {
            HStack(alignment: .top, spacing: DesignTheme.Spacing.md) {
                Image(systemName: icon)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(DesignTheme.accentColor)
                    .frame(width: 20)

                VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                    Text(label)
                        .font(DesignTheme.Typography.bodySmallest)
                        .foregroundColor(.secondary)

                    Text(value)
                        .font(DesignTheme.Typography.body)
                        .lineLimit(nil)
                }

                Spacer()
            }
        }
    }

    private struct ParticipantsSection: View {
        let participants: [EventParticipant]
        let statusColor: (String) -> Color

        var body: some View {
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
                Text("Participants")
                    .font(DesignTheme.Typography.captionSemibold)
                    .foregroundColor(.secondary)
                    .textCase(.uppercase)

                VStack(spacing: DesignTheme.Spacing.md) {
                    ForEach(participants, id: \.userId) { participant in
                        ParticipantCard(
                            participant: participant,
                            statusColor: statusColor
                        )
                    }
                }
            }
        }
    }

    private struct ParticipantCard: View {
        let participant: EventParticipant
        let statusColor: (String) -> Color

        var body: some View {
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.sm) {
                HStack(alignment: .center, spacing: DesignTheme.Spacing.md) {
                    VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                        Text(participant.username)
                            .font(DesignTheme.Typography.body)
                            .foregroundStyle(.primary)

                        Text(participant.role)
                            .font(DesignTheme.Typography.bodySmallest)
                            .foregroundStyle(.secondary)
                    }

                    Spacer()

                    HStack(spacing: DesignTheme.Spacing.xs) {
                        Image(systemName: statusIcon(participant.status.description()))
                            .font(.system(size: 12, weight: .semibold))
                            .foregroundColor(statusColor(participant.status.description()))

                        Text(participant.status.description().capitalized)
                            .font(DesignTheme.Typography.bodySmallest)
                            .foregroundColor(statusColor(participant.status.description()))
                    }
                    .padding(.vertical, DesignTheme.Spacing.xs)
                    .padding(.horizontal, DesignTheme.Spacing.sm)
                    .background(statusColor(participant.status.description()).opacity(0.08))
                    .cornerRadius(DesignTheme.CornerRadius.small)
                }
            }
            .padding(DesignTheme.Spacing.md)
            .background(Color(UIColor.systemGray6))
            .cornerRadius(DesignTheme.CornerRadius.medium)
        }

        private func statusIcon(_ status: String) -> String {
            switch status.lowercased() {
            case "accepted":
                return "checkmark.circle.fill"
            case "declined":
                return "xmark.circle.fill"
            case "pending":
                return "clock.fill"
            default:
                return "questionmark.circle.fill"
            }
        }
    }

    private struct ActionsSection: View {
        let onAccept: () -> Void
        let onDecline: () -> Void

        var body: some View {
            VStack(spacing: DesignTheme.Spacing.md) {
                ButtonFactory.primary(
                    action: onAccept,
                    label: "Accept Invitation"
                )

                ButtonFactory.secondary(
                    action: onDecline,
                    label: "Decline Invitation"
                )
            }
            .padding(.top, DesignTheme.Spacing.md)
        }
    }
}
