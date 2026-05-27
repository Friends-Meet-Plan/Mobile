//
//  Router.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI
import Shared

@Observable
final class Router {
    
    var path = NavigationPath()
    var session: AuthSession?
    var onCreatedEventPushBack: (() -> Void)?
    
    func push(screen: AppRouter) {
        path.append(screen)
    }
    
    func pop() {
        path.removeLast()
    }
    
    func root() {
        path = NavigationPath()
    }
    
    func login() {
        session = nil
    }
}
