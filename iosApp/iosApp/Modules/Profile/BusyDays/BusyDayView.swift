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
        VStack(alignment: .leading, spacing: 16) {
            Text("Activity")
                .font(.headline)
                .fontWeight(.semibold)
            
            ZStack {
                if reducer.isLoading {
                    ProgressView()
                        .frame(maxWidth: .infinity, maxHeight: 120, alignment: .center)
                } else if let errorMessage = reducer.errorMessage {
                    VStack(spacing: 12) {
                        Image(systemName: "exclamationmark.circle")
                            .font(.title3)
                            .foregroundColor(.orange)
                        Text(errorMessage)
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Button("Retry") {
                            reducer.retry()
                        }
                        .tint(.blue)
                        .font(.caption)
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                } else if reducer.busyDays.isEmpty {
                    HStack(spacing: 8) {
                        Image(systemName: "calendar")
                            .foregroundColor(.gray)
                        Text("No busy days recorded")
                            .foregroundColor(.gray)
                            .font(.caption)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .center)
                } else {
                    ActivityGridView(busyDays: reducer.busyDays)
                }
            }
            .frame(maxWidth: .infinity)
        }
        .padding()
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
        
        let columns = Array(repeating: GridItem(.flexible(), spacing: 4), count: gridSize)
        
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 4) {
                ForEach(["S", "M", "T", "W", "T", "F", "S"], id: \.self) { day in
                    Text(day)
                        .font(.caption2)
                        .fontWeight(.semibold)
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.bottom, 4)
            
            LazyVGrid(columns: columns, spacing: 4) {
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
                .font(.caption2)
                .fontWeight(.semibold)
                .foregroundColor(isBusy ? .white : .primary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .aspectRatio(1, contentMode: .fit)
        .background(isBusy ? Color.green : Color.gray.opacity(0.15))
        .cornerRadius(4)
    }
    
    private var dayOfMonth: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "d"
        return formatter.string(from: date)
    }
}
