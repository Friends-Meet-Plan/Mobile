package friends.mobile.designkit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import friends.mobile.designkit.theme.DesignTypography

/**
 * Full-screen loading view — centered spinner + "Loading friends..." text.
 * White background, subheadline secondary text.
 */
@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    message: String = "Loading friends..."
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = message,
            style = DesignTypography.caption,
            color = Color.Gray
        )
    }
}
