//
//  EventState.swift
//  iosApp
//
//  Created by Данил Забинский on 02.06.2026.
//

import SwiftUI

enum EventState {
    
    case active
    case pending
    case archive
    
    var tint: Color {
        switch self {
        case .active:
            DesignTheme.secondaryAccent
        case .pending:
            Color.orange
        case .archive:
            Color.gray
        }
    }
}
