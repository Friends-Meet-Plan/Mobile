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
        ZStack {
            Color(.systemBackground)
                .ignoresSafeArea()

            VStack(spacing: DesignTheme.Spacing.lg) {
                dateHeaderSection

                if reducer.isLoadingFriends {
                    loadingSection
                } else {
                    ScrollView {
                        VStack(spacing: DesignTheme.Spacing.lg) {
                            formFieldsSection
                            friendsSelectionSection
                        }
                        .padding(.horizontal, DesignTheme.Spacing.lg)
                        .padding(.bottom, DesignTheme.Spacing.xl)
                    }

                    createButtonSection
                }

                if reducer.isCreatingEvent {
                    Spacer()
                    ProgressView()
                        .scaleEffect(1.2)
                    Spacer()
                }
            }
            .padding(.horizontal, DesignTheme.Spacing.lg)
            .padding(.vertical, DesignTheme.Spacing.lg)

            if let errorMessage = reducer.errorMessage {
                VStack {
                    FormErrorMessage(message: errorMessage)
                        .padding(DesignTheme.Spacing.lg)
                    Spacer()
                }
            }
        }
        .navigationTitle("Create Event")
        .navigationBarTitleDisplayMode(.inline)
        .task {
            reducer.onNavigateToEventDetail = { eventId in
                router.push(screen: .eventDetail(id: eventId))
            }
        }
        .sheet(isPresented: Binding(
            get: { reducer.showFriendsSheet },
            set: { reducer.showFriendsSheet = $0 }
        )) {
            friendsSelectionSheet
        }
    }

    // MARK: - Header Section

    private var dateHeaderSection: some View {
        VStack(spacing: DesignTheme.Spacing.sm) {
            Text("Event Date")
                .font(DesignTheme.Typography.bodySmall)
                .foregroundColor(.secondary)

            Text(reducer.selectedDate)
                .font(DesignTheme.Typography.heading)
                .foregroundColor(.primary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, DesignTheme.Spacing.lg)
        .background(Color(UIColor.systemGray6).opacity(0.5))
        .cornerRadius(DesignTheme.CornerRadius.medium)
    }

    // MARK: - Loading Section

    private var loadingSection: some View {
        VStack(spacing: DesignTheme.Spacing.md) {
            ProgressView()
                .scaleEffect(1.2)
            Text("Loading available friends...")
                .font(DesignTheme.Typography.bodySmall)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    // MARK: - Form Fields Section

    private var formFieldsSection: some View {
        VStack(spacing: DesignTheme.Spacing.lg) {
            VStack(alignment: .leading, spacing: DesignTheme.Spacing.sm) {
                Label("Title", systemImage: "pencil")
                    .font(DesignTheme.Typography.bodySmall)
                    .foregroundColor(.secondary)

                TextField("Event title", text: Binding(
                    get: { reducer.title },
                    set: { reducer.updateTitle($0) }
                ))
                .formTextField()
            }

            VStack(alignment: .leading, spacing: DesignTheme.Spacing.sm) {
                Label("Description", systemImage: "doc.text")
                    .font(DesignTheme.Typography.bodySmall)
                    .foregroundColor(.secondary)

                TextField("Event description", text: Binding(
                    get: { reducer.description },
                    set: { reducer.updateDescription($0) }
                ))
                .formTextField()
            }

            VStack(alignment: .leading, spacing: DesignTheme.Spacing.sm) {
                Label("Location", systemImage: "location.fill")
                    .font(DesignTheme.Typography.bodySmall)
                    .foregroundColor(.secondary)

                TextField("Event location", text: Binding(
                    get: { reducer.location },
                    set: { reducer.updateLocation($0) }
                ))
                .formTextField()
            }
        }
    }

    // MARK: - Friends Selection Section

    private var friendsSelectionSection: some View {
        VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
            Button(action: { reducer.toggleFriendsSheet() }) {
                HStack(spacing: DesignTheme.Spacing.md) {
                    Image(systemName: "person.2.fill")
                        .font(.system(size: 14, weight: .semibold))

                    Text("Select Friends")
                        .font(DesignTheme.Typography.button)

                    Spacer()

                    if !reducer.selectedFriendIds.isEmpty {
                        Text("\(reducer.selectedFriendIds.count) selected")
                            .font(DesignTheme.Typography.bodySmallest)
                            .foregroundColor(.white)
                            .padding(.horizontal, DesignTheme.Spacing.md)
                            .padding(.vertical, DesignTheme.Spacing.xs)
                            .background(DesignTheme.accentColor)
                            .cornerRadius(DesignTheme.CornerRadius.small)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(DesignTheme.Spacing.md)
                .background(Color(UIColor.systemGray6))
                .cornerRadius(DesignTheme.CornerRadius.medium)
                .foregroundColor(.primary)
            }

            if !reducer.selectedFriendIds.isEmpty {
                selectedFriendsBadgesSection
            }
        }
    }

    private var selectedFriendsBadgesSection: some View {
        VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
            Text("Selected Friends")
                .font(DesignTheme.Typography.bodySmall)
                .foregroundColor(.secondary)

            FlowLayout(spacing: DesignTheme.Spacing.sm) {
                ForEach(
                    reducer.availableFriends.filter { reducer.selectedFriendIds.contains($0.id) },
                    id: \.id
                ) { friend in
                    friendBadge(for: friend)
                }
            }
        }
        .padding(DesignTheme.Spacing.md)
        .background(Color(UIColor.systemGray6).opacity(0.5))
        .cornerRadius(DesignTheme.CornerRadius.medium)
    }

    private func friendBadge(for friend: User) -> some View {
        HStack(spacing: DesignTheme.Spacing.xs) {
            Text(friend.username)
                .font(DesignTheme.Typography.bodySmallest)

            Button(action: { reducer.toggleFriend(friend.id) }) {
                Image(systemName: "xmark.circle.fill")
                    .font(.system(size: 12, weight: .semibold))
            }
            .foregroundColor(.secondary)
        }
        .padding(.horizontal, DesignTheme.Spacing.md)
        .padding(.vertical, DesignTheme.Spacing.xs)
        .background(DesignTheme.accentColor.opacity(0.15))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }

    // MARK: - Create Button Section

    private var createButtonSection: some View {
        ButtonFactory.primaryLoading(
            action: { reducer.submit() },
            label: "Create Event",
            isLoading: reducer.isCreatingEvent,
            isEnabled: reducer.isCreateButtonEnabled
        )
        .padding(.horizontal, DesignTheme.Spacing.lg)
    }

    // MARK: - Friends Selection Sheet

    private var friendsSelectionSheet: some View {
        NavigationStack {
            ZStack {
                Color(.systemBackground)
                    .ignoresSafeArea()

                if reducer.isLoadingFriends {
                    VStack(spacing: DesignTheme.Spacing.md) {
                        ProgressView()
                            .scaleEffect(1.2)
                        Text("Loading friends...")
                            .font(DesignTheme.Typography.bodySmall)
                            .foregroundColor(.secondary)
                    }
                } else if let error = reducer.friendsError {
                    VStack(spacing: DesignTheme.Spacing.lg) {
                        FormErrorMessage(message: error)
                        Spacer()
                    }
                    .padding(DesignTheme.Spacing.lg)
                } else if reducer.availableFriends.isEmpty {
                    VStack(spacing: DesignTheme.Spacing.md) {
                        Image(systemName: "person.slash")
                            .font(.system(size: 48, weight: .light))
                            .foregroundColor(.secondary)

                        Text("No Friends Available")
                            .font(DesignTheme.Typography.button)
                            .foregroundColor(.primary)

                        Text("No friends are available for this date")
                            .font(DesignTheme.Typography.bodySmall)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    friendsList
                }
            }
            .navigationTitle("Select Friends")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") {
                        reducer.showFriendsSheet = false
                    }
                    .font(DesignTheme.Typography.button)
                    .foregroundColor(DesignTheme.accentColor)
                }
            }
        }
    }

    private var friendsList: some View {
        List(reducer.availableFriends, id: \.id) { friend in
            friendListItem(for: friend)
                .listRowSeparator(.hidden)
                .listRowInsets(EdgeInsets(
                    top: DesignTheme.Spacing.xs,
                    leading: DesignTheme.Spacing.lg,
                    bottom: DesignTheme.Spacing.xs,
                    trailing: DesignTheme.Spacing.lg
                ))
        }
        .listStyle(.plain)
    }

    private func friendListItem(for friend: User) -> some View {
        Button(action: { reducer.toggleFriend(friend.id) }) {
            HStack(spacing: DesignTheme.Spacing.md) {
                VStack(alignment: .leading, spacing: DesignTheme.Spacing.xs) {
                    Text(friend.username)
                        .font(DesignTheme.Typography.body)
                        .foregroundColor(.primary)

                    if let bio = friend.bio, !bio.isEmpty {
                        Text(bio)
                            .font(DesignTheme.Typography.bodySmall)
                            .foregroundColor(.secondary)
                            .lineLimit(1)
                    }
                }

                Spacer()

                Image(systemName: reducer.selectedFriendIds.contains(friend.id) ? "checkmark.circle.fill" : "circle")
                    .font(.system(size: 20, weight: .semibold))
                    .foregroundColor(
                        reducer.selectedFriendIds.contains(friend.id)
                            ? DesignTheme.accentColor
                            : Color(.systemGray3)
                    )
            }
            .padding(DesignTheme.Spacing.md)
            .background(Color(UIColor.systemGray6).opacity(0.5))
            .cornerRadius(DesignTheme.CornerRadius.medium)
        }
    }
}

// MARK: - FlowLayout Helper

struct FlowLayout: Layout {
    let spacing: CGFloat

    func sizeThatFits(
        proposal: ProposedViewSize,
        subviews: Subviews,
        cache: inout ()
    ) -> CGSize {
        let maxWidth = proposal.width ?? 0
        var height: CGFloat = 0
        var currentLineWidth: CGFloat = 0
        let lineHeight: CGFloat = 32

        for subview in subviews {
            let size = subview.sizeThatFits(.unspecified)

            if currentLineWidth + size.width + spacing > maxWidth && currentLineWidth > 0 {
                height += lineHeight + spacing
                currentLineWidth = 0
            }

            currentLineWidth += size.width + spacing
        }

        height += lineHeight
        return CGSize(width: maxWidth, height: height)
    }

    func placeSubviews(
        in bounds: CGRect,
        proposal: ProposedViewSize,
        subviews: Subviews,
        cache: inout ()
    ) {
        var currentX: CGFloat = bounds.minX
        var currentY: CGFloat = bounds.minY
        let maxWidth = bounds.width
        let lineHeight: CGFloat = 32

        for subview in subviews {
            let size = subview.sizeThatFits(.unspecified)

            if currentX + size.width > bounds.maxX && currentX > bounds.minX {
                currentY += lineHeight + spacing
                currentX = bounds.minX
            }

            subview.place(
                at: CGPoint(x: currentX, y: currentY),
                proposal: .unspecified
            )

            currentX += size.width + spacing
        }
    }
}
