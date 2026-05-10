//
//  AuthUserMapper.swift
//  iosApp
//
//  Created by Данил Забинский on 08.05.2026.
//

import Shared

extension [UserDto] {
    
    func mapToAuthUser() -> [AuthUser] {
        self.map {
            AuthUser(
                id: $0.id,
                username: $0.username,
                avatarUrl: $0.avatarUrl,
                bio: $0.bio
            )
        }
    }
}
