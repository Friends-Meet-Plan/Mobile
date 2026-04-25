//
//  Router.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import SwiftUI

@Observable
final class Router {
    
    var path = NavigationPath()
    
    func push(screen: AppRouter) {
        path.append(screen)
    }
    
    func pop() {
        path.removeLast()
    }
    
    func root() {
        path = NavigationPath()
    }
}
