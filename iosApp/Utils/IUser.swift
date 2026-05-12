//
//  IUser.swift
//  iosApp
//
//  Created by Данил Забинский on 11.05.2026.
//

import Shared

protocol IUser {
    
    var id: String { get }
    var username: String { get }
    var bio: String? { get }
    var avatarUrl: String? { get }
}

extension Shared.User: IUser {}
extension Shared.Profile: IUser {}
