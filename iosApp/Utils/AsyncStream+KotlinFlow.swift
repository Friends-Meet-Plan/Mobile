//
//  AsyncStream+KotlinFlow.swift
//  iosApp
//
//  Created by Данил Забинский on 06.05.2026.
//

import Shared

extension Kotlinx_coroutines_coreStateFlow {
    func asAsyncStream(scope: Kotlinx_coroutines_coreCoroutineScope) -> AsyncStream<Any?> {
        AsyncStream { continuation in
            let job = FlowUtilsKt.subscribe(self, scope: scope) { value in
                continuation.yield(value)
            }
            continuation.onTermination = { _ in
                job.cancel(cause: nil)
            }
        }
    }
}

extension Kotlinx_coroutines_coreSharedFlow {
    func asAsyncStream(scope: Kotlinx_coroutines_coreCoroutineScope) -> AsyncStream<Any?> {
        AsyncStream { continuation in
            let job = FlowUtilsKt.subscribe(self, scope: scope) { value in
                continuation.yield(value)
            }
            continuation.onTermination = { _ in
                job.cancel(cause: nil)
            }
        }
    }
}
