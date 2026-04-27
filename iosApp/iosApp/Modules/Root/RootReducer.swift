import Foundation
import Shared

@Observable
final class RootReducer {

    var session: AuthSession?

    private let viewModel = RootViewModel()
    private var stateJob: Kotlinx_coroutines_coreJob?

    init() {
        stateJob = viewModel.viewStates.subscribe(
            onItem: { [weak self] item in
                guard let state = item as? RootViewState else { return }
                self?.session = state.session
            },
            onComplete: {},
            onThrow: { _ in },
        )
    }

    deinit {
        stateJob?.cancel(cause: nil)
        viewModel.clear()
    }

    func onLoginSuccess(_ session: AuthSession) {
        viewModel.obtainEvent(event: OnSessionStarted(session: session))
    }

    func logout() {
        viewModel.obtainEvent(event: OnLogoutClick())
    }
}
