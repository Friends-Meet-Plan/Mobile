package friends.mobile.designkit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Backward-compatible re-exports.
 * New code should import directly from friends.mobile.designkit.components.
 */

@Composable
fun FormErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    friends.mobile.designkit.components.FormErrorMessage(message, modifier)
}

@Composable
fun FormInfoMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    friends.mobile.designkit.components.FormInfoMessage(message, modifier)
}
