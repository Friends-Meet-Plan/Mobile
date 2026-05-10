//
//  ProfileView.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI
import Shared

struct ProfileView: View {
    @Environment(Router.self) var router
    
    var user: AuthUser
    
    var body: some View {
        VStack {
            Text("Profile")
                .font(.largeTitle)
                .bold()
        }
        .navigationTitle("Profile")
    }
}
