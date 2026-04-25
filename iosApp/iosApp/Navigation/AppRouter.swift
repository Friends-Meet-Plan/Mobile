//
//  AppRouter.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

enum AppRouter: Hashable {
    
    case login
    
    var destination: some View {
        switch self {
        case .login:
            LoginView()
        }
    }
}
