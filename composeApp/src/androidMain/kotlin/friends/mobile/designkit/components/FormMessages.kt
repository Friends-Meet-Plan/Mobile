package friends.mobile.designkit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import friends.mobile.designkit.theme.DesignColors
import friends.mobile.designkit.theme.DesignTypography

/**
 * Red error message box with Warning icon and error-colored text.
 * Background is errorLight (red at 8% opacity), corner radius 10dp.
 */
@Composable
fun FormErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DesignColors.errorLight, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = DesignColors.error,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = message,
            style = DesignTypography.bodySmall,
            color = DesignColors.error
        )
    }
}

/**
 * Blue info message box with Info icon.
 * Background is infoLight (blue at 6% opacity), icon in accentColorHex, text in accentColorHex at 80% opacity.
 */
@Composable
fun FormInfoMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DesignColors.infoLight, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = DesignColors.primaryHex,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = message,
            style = DesignTypography.bodySmallest,
            color = DesignColors.primaryHex.copy(alpha = 0.8f)
        )
    }
}
