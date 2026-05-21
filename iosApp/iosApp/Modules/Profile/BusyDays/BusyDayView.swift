//
//  BusyDayView.swift
//  iosApp
//
//  Created by Данил Забинский on 16.05.2026.
//

import SwiftUI
import Foundation
import Shared

struct BusyDayView: View {

    @State private var reducer: BusyDaysReducer
    private let userId: String

    init(userId: String) {
        self.userId = userId
        let reducer = BusyDaysReducer(userId: userId)
        self._reducer = State(initialValue: reducer)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: DesignTheme.Spacing.lg) {
            if reducer.isLoading {
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: 120, alignment: .center)
            } else if let errorMessage = reducer.errorMessage {
                VStack(spacing: DesignTheme.Spacing.md) {
                    HStack(spacing: DesignTheme.Spacing.sm) {
                        Image(systemName: "exclamationmark.circle")
                            .font(.title3)
                            .foregroundColor(DesignTheme.error)
                        Text(errorMessage)
                            .font(DesignTheme.Typography.caption)
                            .foregroundColor(.secondary)
                    }
                    Button(action: { reducer.retry() }) {
                        Text("Retry")
                            .font(DesignTheme.Typography.caption)
                            .frame(maxWidth: .infinity)
                            .frame(height: 36)
                            .background(DesignTheme.accentColor)
                            .foregroundColor(.white)
                            .cornerRadius(DesignTheme.CornerRadius.medium)
                    }
                }
                .padding(DesignTheme.Spacing.lg)
                .frame(maxWidth: .infinity)
                .background(DesignTheme.errorLight)
                .cornerRadius(DesignTheme.CornerRadius.medium)
            } else if reducer.busyDays.isEmpty {
                HStack(spacing: DesignTheme.Spacing.sm) {
                    Image(systemName: "calendar")
                        .foregroundColor(.gray)
                    Text("No busy days recorded")
                        .font(DesignTheme.Typography.caption)
                        .foregroundColor(.gray)
                }
                .padding(DesignTheme.Spacing.lg)
                .frame(maxWidth: .infinity, alignment: .center)
                .background(Color.gray.opacity(0.05))
                .cornerRadius(DesignTheme.CornerRadius.medium)
            } else {
                ActivityGridView(busyDays: reducer.busyDays)
                    .padding(DesignTheme.Spacing.lg)
                    .background(Color.gray.opacity(0.03))
                    .cornerRadius(DesignTheme.CornerRadius.medium)
            }
        }
    }
}

struct ActivityGridView: View {

    let busyDays: [String]

    var body: some View {
        let calendar = Calendar.current
        let today = Date()
        let endDate = calendar.date(byAdding: .day, value: 30, to: today) ?? today

        let gridSize: Int = 7
        let startDate = today
        let dateRange = datesInRange(from: startDate, to: endDate)

        let columns = Array(repeating: GridItem(.flexible(), spacing: DesignTheme.Spacing.xs), count: gridSize)

        VStack(alignment: .leading, spacing: DesignTheme.Spacing.md) {
            HStack(spacing: DesignTheme.Spacing.xs) {
                ForEach(["S", "M", "T", "W", "T", "F", "S"], id: \.self) { day in
                    Text(day)
                        .font(DesignTheme.Typography.bodySmallest)
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.bottom, DesignTheme.Spacing.xs)

            LazyVGrid(columns: columns, spacing: DesignTheme.Spacing.xs) {
                ForEach(dateRange, id: \.self) { date in
                    DayCell(date: date, isBusy: isBusy(date))
                }
            }
        }
    }
    
    private func datesInRange(from startDate: Date, to endDate: Date) -> [Date] {
        let calendar = Calendar.current
        var dates: [Date] = []
        var currentDate = startDate
        
        while currentDate <= endDate {
            dates.append(currentDate)
            currentDate = calendar.date(byAdding: .day, value: 1, to: currentDate) ?? currentDate
        }
        
        return dates
    }
    
    private func isBusy(_ date: Date) -> Bool {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let dateString = formatter.string(from: date)
        return busyDays.contains(dateString)
    }
}

struct DayCell: View {

    let date: Date
    let isBusy: Bool

    var body: some View {
        VStack {
            Text(dayOfMonth)
                .font(DesignTheme.Typography.bodySmallest)
                .fontWeight(.semibold)
                .foregroundColor(isBusy ? .white : .primary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .aspectRatio(1, contentMode: .fit)
        .background(isBusy ? DesignTheme.secondaryAccent : Color.gray.opacity(0.1))
        .cornerRadius(DesignTheme.CornerRadius.small)
    }

    private var dayOfMonth: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "d"
        return formatter.string(from: date)
    }
}
