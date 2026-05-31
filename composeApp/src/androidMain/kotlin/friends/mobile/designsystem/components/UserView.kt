package friends.mobile.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import friends.mobile.designsystem.theme.DesignTheme

enum class Dimension {
    Horizontal,
    Vertical
}

@Composable
fun UserView(
    username: String,
    bio: String?,
    dimension: Dimension = Dimension.Vertical,
    modifier: Modifier = Modifier
) {
    val avatar: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.firstOrNull()?.uppercase() ?: "?",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    val textStack: @Composable () -> Unit = {
        Column(
            horizontalAlignment = if (dimension == Dimension.Vertical) Alignment.CenterHorizontally else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = username,
                style = DesignTheme.Typography.heading,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!bio.isNullOrEmpty()) {
                Text(
                    text = bio,
                    style = DesignTheme.Typography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    when (dimension) {
        Dimension.Horizontal -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                avatar()
                textStack()
            }
        }
        Dimension.Vertical -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                avatar()
                textStack()
            }
        }
    }
}
