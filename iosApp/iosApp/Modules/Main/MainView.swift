//
//  MainView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct MainView: View {
    
    var body: some View {
        VStack {
            Text("Welcome!")
                .font(.title2)
                .fontWeight(.semibold)
        }
        .navigationTitle("Home")
    }
}
