//
//  ApplicationState.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import Foundation

@Observable
final class ApplicationState {
    
    var user: User
    
    init(user: User) {
        self.user = user
    }
}
