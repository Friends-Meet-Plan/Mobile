package friends.mobile.feature.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.auth.presentation.RootEvent
import friends.mobile.feature.auth.presentation.RootViewModel
import friends.mobile.feature.auth.presentation.RootViewState
import friends.mobile.navigation.AppNavigation
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootScreen(
    viewModel: RootViewModel = koinViewModel()
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is RootViewState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is RootViewState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DesignTheme.Spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                ErrorBanner(
                    message = currentState.message,
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = { viewModel.obtainEvent(RootEvent.OnRetry) }
                )
            }
        }
        is RootViewState.Content -> {
            val session = currentState.session
            if (session == null) {
                LoginScreen(
                    onLoginSuccess = { authSession ->
                        viewModel.obtainEvent(RootEvent.OnSessionStarted(authSession))
                    },
                )
            } else {
                AppNavigation(
                    onLogout = {
                        viewModel.obtainEvent(RootEvent.OnLogoutClick)
                    }
                )
            }
        }
    }
}
