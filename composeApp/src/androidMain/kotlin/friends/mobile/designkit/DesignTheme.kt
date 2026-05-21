package friends.mobile.designkit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object DesignTheme {
    object Colors {
        val primary = Color(0xFF007AFF)
        val primaryHex = Color(red = 0.0f, green = 0.48f, blue = 1.0f)
        val error = Color(0xFFFF3B30)
        val errorLight = Color.Red.copy(alpha = 0.08f)
        val infoLight = primaryHex.copy(alpha = 0.06f)
        val textField = Color(0xFFF2F2F7)
    }

    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 20.dp
        val xxl = 28.dp
        val xxxl = 32.dp
    }

    object Typography {
        val body = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
        val bodySmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
        val bodySmallest = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
        val button = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        val caption = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)
        val captionSemibold = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        val heading = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }

    object CornerRadius {
        val small = 10.dp
        val medium = 12.dp
        val capsule = 27.dp
    }
}

@Composable
fun FriendsAppTheme(
    content: @Composable () -> Unit
) {
    val lightColorScheme = lightColorScheme(
        primary = DesignTheme.Colors.primary,
        onPrimary = Color.White,
        background = Color.White,
        surface = Color.White,
        onSurface = Color.Black,
        error = DesignTheme.Colors.error,
        onError = Color.White
    )

    MaterialTheme(
        colorScheme = lightColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
