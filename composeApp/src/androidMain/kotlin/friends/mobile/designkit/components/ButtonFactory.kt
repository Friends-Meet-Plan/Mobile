package friends.mobile.designkit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import friends.mobile.designkit.theme.DesignColors
import friends.mobile.designkit.theme.DesignTypography

object ButtonFactory {

    /**
     * Primary action button — blue capsule background, white text, full width.
     * Shows a loading spinner alongside text when [isLoading] is true.
     * Disabled at 0.3 opacity when [isEnabled] is false.
     */
    @Composable
    fun primary(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        isLoading: Boolean = false,
        isEnabled: Boolean = true,
        icon: @Composable (() -> Unit)? = null
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(54.dp)
                .alpha(if (isEnabled) 1f else 0.3f),
            enabled = isEnabled && !isLoading,
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DesignColors.primary,
                contentColor = Color.White,
                disabledContainerColor = DesignColors.primary,
                disabledContentColor = Color.White
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
                    Text("Loading...", style = DesignTypography.button)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icon?.invoke()
                    Text(text, style = DesignTypography.button)
                }
            }
        }
    }

    /**
     * Secondary destructive-light button — errorLight background, error-colored text, full width.
     */
    @Composable
    fun secondary(
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
                containerColor = DesignColors.errorLight,
                contentColor = DesignColors.error,
                disabledContainerColor = DesignColors.errorLight,
                disabledContentColor = DesignColors.error
            )
        ) {
            Text(text, style = DesignTypography.button)
        }
    }

    /**
     * Compact button — systemGray6 background, blue text, fixed size (not full width),
     * horizontal padding 12dp. Used for inline/secondary actions.
     */
    @Composable
    fun compact(
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
                containerColor = DesignColors.systemGray6,
                contentColor = DesignColors.primary,
                disabledContainerColor = DesignColors.systemGray6,
                disabledContentColor = DesignColors.primary
            )
        ) {
            Text(text, style = DesignTypography.button)
        }
    }

    /**
     * Disabled-state button — gray background, white text, not clickable.
     * Optionally shows an icon on the left.
     */
    @Composable
    fun disabled(
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
                Text(text, style = DesignTypography.button)
            }
        }
    }

    /**
     * Destructive button — red capsule background, white text, full width.
     * Shows spinner + "Removing..." when [isLoading] is true.
     */
    @Composable
    fun destructive(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        isLoading: Boolean = false,
        isEnabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(54.dp)
                .alpha(if (isEnabled) 1f else 0.3f),
            enabled = isEnabled && !isLoading,
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DesignColors.error,
                contentColor = Color.White,
                disabledContainerColor = DesignColors.error,
                disabledContentColor = Color.White
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
                    Text("Removing...", style = DesignTypography.button)
                }
            } else {
                Text(text, style = DesignTypography.button)
            }
        }
    }
}
