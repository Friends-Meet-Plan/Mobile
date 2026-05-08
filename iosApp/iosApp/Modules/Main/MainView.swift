//
//  MainView.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

struct MainView: View {
    @Environment(Router.self) var router
    
    var user: AuthUser
    
    var body: some View {
        Text("Main view")
            .navigationTitle("Hello")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        router.push(screen: .friends)
                    } label: {
                        Image(systemName: "person.badge.plus")
                    }
                }
            }
    }
}
