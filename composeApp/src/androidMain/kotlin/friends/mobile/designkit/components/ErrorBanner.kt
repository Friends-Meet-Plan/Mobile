package friends.mobile.designkit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
 * Full-width inline error banner used at screen level.
 * Warning icon (error color) + message text + trailing Spacer.
 * Background errorLight, corner radius 10dp, padding 12dp.
 */
@Composable
fun ErrorBanner(
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
        Spacer(modifier = Modifier.weight(1f))
    }
}
