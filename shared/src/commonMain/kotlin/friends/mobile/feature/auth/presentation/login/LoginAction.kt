package friends.mobile.feature.auth.presentation.login

import friends.mobile.feature.auth.domain.model.AuthSession

sealed class LoginAction {
    data class NavigateToHome(val session: AuthSession) : LoginAction()
    data class ShowMessage(val message: String) : LoginAction()
}
