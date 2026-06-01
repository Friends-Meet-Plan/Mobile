package friends.mobile.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import friends.mobile.R
import friends.mobile.designsystem.theme.DesignTheme

object ButtonFactory {

    /**
     * Primary action button — blue capsule background, white text, full width.
     * Shows a loading spinner alongside text when [isLoading] is true.
     * Disabled at 0.3 opacity when [isEnabled] is false.
     */
    @Composable
    fun Primary(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        isLoading: Boolean = false,
        isEnabled: Boolean = true,
        icon: @Composable (() -> Unit)? = null,
        buttonHeight: Dp = 54.dp
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .alpha(if (isEnabled) 1f else 0.3f),
            enabled = isEnabled && !isLoading,
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Text(stringResource(R.string.loading), style = DesignTheme.Typography.button)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icon?.invoke()
                    Text(text, style = DesignTheme.Typography.button)
                }
            }
        }
    }

    /**
     * Secondary destructive-light button — errorLight background, error-colored text, full width.
     */
    @Composable
    fun Secondary(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        isEnabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(54.dp)
                .alpha(if (isEnabled) 1f else 0.3f),
            enabled = isEnabled,
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                disabledContentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text, style = DesignTheme.Typography.button)
        }
    }

    /**
     * Compact button — systemGray6 background, blue text, fixed size (not full width),
     * horizontal padding 12dp. Used for inline/secondary actions.
     */
    @Composable
    fun Compact(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        isEnabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .height(44.dp)
                .alpha(if (isEnabled) 1f else 0.3f),
            enabled = isEnabled,
            shape = RoundedCornerShape(27.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text, style = DesignTheme.Typography.button)
        }
    }

    /**
     * Disabled-state button — gray background, white text, not clickable.
     * Optionally shows an icon on the left.
     */
    @Composable
    fun Disabled(
        text: String,
        modifier: Modifier = Modifier,
        icon: @Composable (() -> Unit)? = null
    ) {
        Button(
            onClick = {},
            modifier = modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = false,
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Gray.copy(alpha = 0.6f),
                disabledContentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon?.invoke()
                Text(text, style = DesignTheme.Typography.button)
            }
        }
    }

    /**
     * Destructive button — red capsule background, white text, full width.
     * Shows spinner + "Removing..." when [isLoading] is true.
     */
    @Composable
    fun Destructive(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        isLoading: Boolean = false,
        isEnabled: Boolean = true,
        buttonHeight: Dp = 54.dp
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .alpha(if (isEnabled) 1f else 0.3f),
            enabled = isEnabled && !isLoading,
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.error,
                disabledContentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Text(stringResource(R.string.loading_removing), style = DesignTheme.Typography.button)
                }
            } else {
                Text(text, style = DesignTheme.Typography.button)
            }
        }
    }
}
