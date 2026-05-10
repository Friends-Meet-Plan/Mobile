//
//  Segment.swift
//  iosApp
//
//  Created by Данил Забинский on 08.05.2026.
//

import Shared

enum Segment: String, CaseIterable {
    
    case friends = "Friends"
    case incoming = "Incoming"
    case outgoing = "Outgoing"
    
    var requestTab: Shared.RequestTab {
        switch self {
        case .friends:
            return .friends
        case .incoming:
            return .incoming
        case .outgoing:
            return .outgoing
        }
    }
    
    static func from(_ tab: Shared.RequestTab) -> Segment {
        switch tab {
        case .friends:
            return .friends
        case .incoming:
            return .incoming
        case .outgoing:
            return .outgoing
        default:
            fatalError()
        }
    }
}
