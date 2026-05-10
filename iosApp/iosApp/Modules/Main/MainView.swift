//
//  MainView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct MainView: View {
    var user: AuthUser
    
    var body: some View {
        VStack {
            Text("Welcome, \(user.username)!")
                .font(.title2)
                .fontWeight(.semibold)
        }
        .navigationTitle("Home")
    }
}
