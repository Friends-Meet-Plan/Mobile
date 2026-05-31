package friends.mobile.designkit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Backward-compatible re-export.
 * New code should use ButtonFactory.primary() from friends.mobile.designkit.components.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    friends.mobile.designkit.components.ButtonFactory.primary(
        text = text,
        onClick = onClick,
        modifier = modifier,
        isLoading = isLoading,
        isEnabled = isEnabled,
        icon = icon
    )
}
