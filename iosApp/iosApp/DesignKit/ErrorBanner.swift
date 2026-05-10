//
//  ErrorBanner.swift
//  iosApp
//
//  Created by Данил Забинский on 08.05.2026.
//

import SwiftUI

struct ErrorBanner: View {
    let message: String
    
    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: "exclamationmark.circle.fill")
                .foregroundColor(.white)
            
            Text(message)
                .font(.subheadline)
                .foregroundColor(.white)
            
            Spacer()
        }
        .padding(12)
        .background(Color.red.opacity(0.8))
    }
}
