//
//  MainView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct MainView: View {
    
    @State private var isCreatingEventInProgress = false
    @State private var selectedDate = Date()
    @Environment(Router.self) private var router
    
    private let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()
    
    var body: some View {
        VStack {
            Button {
                isCreatingEventInProgress = true
            } label: {
                Text("Create event")
            }
        }
        .navigationTitle("Home")
        .sheet(isPresented: $isCreatingEventInProgress) {
            DatePicker(
                "Select event date",
                selection: $selectedDate,
                in: Date()...,
                displayedComponents: [.date, .hourAndMinute]
            )
            .datePickerStyle(.graphical)
            .padding()
            .presentationDetents([.medium])
            
            Button("Create") {
                router.push(screen: .createEvent(date: dateFormatter.string(from: selectedDate)))
                isCreatingEventInProgress = false
            }
        }
    }
}
