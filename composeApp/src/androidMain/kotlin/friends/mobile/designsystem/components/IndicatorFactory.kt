package friends.mobile.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import friends.mobile.designsystem.theme.DesignTheme

object IndicatorFactory {

    private val orange = Color(0xFFFF9500)

    /**
     * Active status — green checkmark circle icon, green text, green tinted background.
     */
    @Composable
    fun Active(modifier: Modifier = Modifier) {
        val green = MaterialTheme.colorScheme.tertiary
        StatusChip(
            label = "Active",
            textColor = green,
            iconTint = green,
            background = green.copy(alpha = 0.1f),
            icon = Icons.Default.CheckCircle,
            modifier = modifier
        )
    }

    /**
     * Pending status — orange clock icon, white text, orange background.
     */
    @Composable
    fun Pending(modifier: Modifier = Modifier) {
        StatusChip(
            label = "Pending",
            textColor = Color.White,
            iconTint = Color.White,
            background = orange,
            icon = Icons.Default.AccessTimeFilled,
            modifier = modifier
        )
    }

    /**
     * Accepted status — green checkmark circle icon, green text, green tinted background.
     */
    @Composable
    fun Accepted(modifier: Modifier = Modifier) {
        val green = MaterialTheme.colorScheme.tertiary
        StatusChip(
            label = "Accepted",
            textColor = green,
            iconTint = green,
            background = green.copy(alpha = 0.1f),
            icon = Icons.Default.CheckCircle,
            modifier = modifier
        )
    }

    /**
     * Declined status — red X circle icon, error text, errorLight background.
     */
    @Composable
    fun Declined(modifier: Modifier = Modifier) {
        val errorColor = MaterialTheme.colorScheme.error
        StatusChip(
            label = "Declined",
            textColor = errorColor,
            iconTint = errorColor,
            background = errorColor.copy(alpha = 0.1f),
            icon = Icons.Default.Cancel,
            modifier = modifier
        )
    }

    /**
     * Sent status — gray paperplane/send icon, gray text, systemGray6 background.
     */
    @Composable
    fun Sent(modifier: Modifier = Modifier) {
        StatusChip(
            label = "Sent",
            textColor = Color.Gray,
            iconTint = Color.Gray,
            background = MaterialTheme.colorScheme.surfaceVariant,
            icon = Icons.AutoMirrored.Filled.Send,
            modifier = modifier
        )
    }

    /**
     * Universal status chip with custom label, color and icon.
     * Background is automatically set to color at 10% opacity.
     */
    @Composable
    fun Status(
        label: String,
        color: Color,
        icon: ImageVector,
        modifier: Modifier = Modifier
    ) {
        StatusChip(
            label = label,
            textColor = color,
            iconTint = color,
            background = color.copy(alpha = 0.1f),
            icon = icon,
            modifier = modifier
        )
    }

    @Composable
    private fun StatusChip(
        label: String,
        textColor: Color,
        iconTint: Color,
        background: Color,
        icon: ImageVector,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .background(background, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = DesignTheme.Typography.bodySmallest,
                color = textColor
            )
        }
    }
}
