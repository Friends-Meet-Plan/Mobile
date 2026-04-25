//
//  User.swift
//  iosApp
//
//  Created by Данил Забинский on 25.04.2026.
//

import Foundation

struct User {
    
    var name: String
    var password: String
    
    init(name: String = "", password: String = "") {
        self.name = name
        self.password = password
    }
}
