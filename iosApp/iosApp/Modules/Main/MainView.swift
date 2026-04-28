//
//  MainView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

struct MainView: View {
    let username: String

    var body: some View {
        Text("Main view, \(username)")
    }
}

#Preview {
    MainView(username: "Preview")
}
