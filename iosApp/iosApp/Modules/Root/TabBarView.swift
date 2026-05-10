//
//  TabBarView.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

private enum Tab {
    case main
    case friends
    case profile
}

struct TabBarView: View {
    var user: AuthUser
    
    @Environment(Router.self) var router
    @State private var selectedTab: Tab = .main
    
    var body: some View {
        TabView(selection: $selectedTab) {
            MainView(user: user)
                .tabItem {
                    Label("Main", systemImage: "house.fill")
                }
                .tag(Tab.main)
            FriendsView()
                .tabItem {
                    Label("Friends", systemImage: "person.2.fill")
                }
                .tag(Tab.friends)
            ProfileView(user: user)
                .tabItem {
                    Label("Profile", systemImage: "person.fill")
                }
                .tag(Tab.profile)
        }
    }
}
