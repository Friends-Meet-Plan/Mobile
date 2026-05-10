//
//  Loading.swift
//  iosApp
//
//  Created by Данил Забинский on 08.05.2026.
//

import SwiftUI

struct LoadingView: View {
    
    var body: some View {
        VStack(spacing: 12) {
            ProgressView()
            Text("Loading friends...")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }
}
