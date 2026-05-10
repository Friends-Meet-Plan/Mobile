//
//  UserCircle.swift
//  iosApp
//
//  Created by Данил Забинский on 10.05.2026.
//

import SwiftUI

struct UserCircle: View {
    let name: String
    
    var body: some View {
        Circle()
            .fill(.gray.opacity(0.2))
            .frame(width: 80, height: 80)
            .overlay {
                Text(name.prefix(1).uppercased())
                    .font(.title.weight(.semibold))
            }
    }
}
