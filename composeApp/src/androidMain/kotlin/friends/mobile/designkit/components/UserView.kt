package friends.mobile.designkit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import friends.mobile.designkit.theme.DesignTypography

enum class Dimension {
    Horizontal,
    Vertical
}

/**
 * User avatar view with username and optional bio.
 *
 * - [Dimension.Horizontal] — avatar on the left, text stack on the right (HStack layout)
 * - [Dimension.Vertical] — avatar on top, text stack below with a trailing Spacer (VStack layout)
 *
 * Avatar: 80x80 circle with a gray fill and the first letter of the username centered inside.
 */
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
                .background(Color.Gray.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.firstOrNull()?.uppercase() ?: "?",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }

    val textStack: @Composable () -> Unit = {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = username,
                style = DesignTypography.heading
            )
            if (!bio.isNullOrEmpty()) {
                Text(
                    text = bio,
                    style = DesignTypography.caption,
                    color = Color.Gray
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
                Spacer(modifier = Modifier.weight(1f, fill = false))
            }
        }
    }
}
