//package friends.mobile.designkit
//
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.lightColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
///**
// * Central design-token object used across all screens.
// * Values mirror the iOS DesignTheme.swift constants exactly.
// *
// * For structured access via the sub-packages see:
// *   friends.mobile.designkit.theme.DesignColors
// *   friends.mobile.designkit.theme.DesignTypography
// */
//object DesignTheme {
//
//    object Colors {
//        val primary = Color(0xFF007AFF)
//        val primaryHex = Color(red = 0.0f, green = 0.48f, blue = 1.0f)
//        val error = Color(0xFFFF3B30)
//        val errorLight = Color.Red.copy(alpha = 0.08f)
//        val infoLight = Color(red = 0.0f, green = 0.48f, blue = 1.0f).copy(alpha = 0.06f)
//        val textField = Color(0xFFF2F2F7)
//        val secondaryAccent = Color(0xFF34C759)
//        val systemGray6 = Color(0xFFF2F2F7)
//    }
//
//    object Spacing {
//        val xs: Dp = 4.dp
//        val sm: Dp = 8.dp
//        val md: Dp = 12.dp
//        val lg: Dp = 16.dp
//        val xl: Dp = 20.dp
//        val xxl: Dp = 28.dp
//        val xxxl: Dp = 32.dp
//    }
//
//    object Typography {
//        val body = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
//        val bodySmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
//        val bodySmallest = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
//        val button = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
//        val caption = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)
//        val captionSemibold = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
//        val heading = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
//    }
//
//    object CornerRadius {
//        val small: Dp = 10.dp
//        val medium: Dp = 12.dp
//        val capsule: Dp = 27.dp
//    }
//}
//
///**
// * App-wide Material 3 theme wrapper.
// * Full color scheme mirrors iOS UIColor system palette in light mode —
// * neutral grays replace Material You purple/pink defaults.
// */
//@Composable
//fun FriendsAppTheme(
//    content: @Composable () -> Unit
//) {
//    // iOS system colors mapped to Material3 slots:
//    //   systemBackground   → #FFFFFF  (surface / background)
//    //   systemGray6        → #F2F2F7  (surfaceVariant — replaces purple)
//    //   systemGray5        → #E5E5EA  (secondaryContainer — replaces purple)
//    //   systemGray3        → #C7C7CC  (outline)
//    //   systemGray4        → #D1D1D6  (outlineVariant)
//    //   systemGray         → #8E8E93  (onSurfaceVariant / secondary)
//    //   label              → #1C1C1E  (onBackground / onSurface)
//    val colorScheme = lightColorScheme(
//        primary = Color(0xFF007AFF),
//        onPrimary = Color.White,
//        primaryContainer = Color(0xFFD0E4FF),
//        onPrimaryContainer = Color(0xFF001D36),
//        secondary = Color(0xFF8E8E93),
//        onSecondary = Color.White,
//        secondaryContainer = Color(0xFFE5E5EA),
//        onSecondaryContainer = Color(0xFF1C1C1E),
//        tertiary = Color(0xFF34C759),
//        onTertiary = Color.White,
//        tertiaryContainer = Color(0xFFD4F5DD),
//        onTertiaryContainer = Color(0xFF002112),
//        error = Color(0xFFFF3B30),
//        onError = Color.White,
//        errorContainer = Color(0xFFFFEDEC),
//        onErrorContainer = Color(0xFF410002),
//        background = Color.White,
//        onBackground = Color(0xFF1C1C1E),
//        surface = Color.White,
//        onSurface = Color(0xFF1C1C1E),
//        surfaceVariant = Color(0xFFF2F2F7),
//        onSurfaceVariant = Color(0xFF8E8E93),
//        outline = Color(0xFFC7C7CC),
//        outlineVariant = Color(0xFFD1D1D6),
//        scrim = Color.Black,
//        inverseSurface = Color(0xFF1C1C1E),
//        inverseOnSurface = Color(0xFFF2F2F7),
//        inversePrimary = Color(0xFF8FBFFF),
//        surfaceTint = Color(0xFF007AFF),
//    )
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = androidx.compose.material3.Typography(),
//        content = content
//    )
//}
