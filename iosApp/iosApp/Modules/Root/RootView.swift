//
//  RootView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

struct RootView: View {
    @State private var reducer = RootReducer()

    var body: some View {
        LoginView()
    }
}

#Preview {
    RootView()
}
