package friends.mobile.designkit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object DesignTheme {

    object Spacing {
        val xs: Dp = 4.dp
        val sm: Dp = 8.dp
        val md: Dp = 12.dp
        val lg: Dp = 16.dp
        val xl: Dp = 20.dp
        val xxl: Dp = 28.dp
        val xxxl: Dp = 32.dp
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
        val small: Dp = 10.dp
        val medium: Dp = 12.dp
        val capsule: Dp = 27.dp
    }
}

private val LightColorScheme = lightColorScheme(
    primary = DesignColors.primary,
    onPrimary = DesignColors.onPrimary,
    primaryContainer = DesignColors.primaryContainer,
    onPrimaryContainer = DesignColors.onPrimaryContainer,
    secondary = DesignColors.secondary,
    onSecondary = DesignColors.onSecondary,
    secondaryContainer = DesignColors.secondaryContainer,
    onSecondaryContainer = DesignColors.onSecondaryContainer,
    tertiary = DesignColors.tertiary,
    onTertiary = DesignColors.onTertiary,
    tertiaryContainer = DesignColors.tertiaryContainer,
    onTertiaryContainer = DesignColors.onTertiaryContainer,
    error = DesignColors.error,
    onError = DesignColors.onError,
    errorContainer = DesignColors.errorContainer,
    onErrorContainer = DesignColors.onErrorContainer,
    background = DesignColors.background,
    onBackground = DesignColors.onBackground,
    surface = DesignColors.surface,
    onSurface = DesignColors.onSurface,
    surfaceVariant = DesignColors.surfaceVariant,
    onSurfaceVariant = DesignColors.onSurfaceVariant,
    surfaceBright = DesignColors.surfaceBright,
    surfaceDim = DesignColors.surfaceDim,
    surfaceContainer = DesignColors.surfaceContainer,
    surfaceContainerLow = DesignColors.surfaceContainerLow,
    surfaceContainerHigh = DesignColors.surfaceContainerHigh,
    surfaceContainerHighest = DesignColors.surfaceContainerHighest,
    outline = DesignColors.outline,
    outlineVariant = DesignColors.outlineVariant,
    scrim = DesignColors.scrim,
    inverseSurface = DesignColors.inverseSurface,
    inverseOnSurface = DesignColors.inverseOnSurface,
    inversePrimary = DesignColors.inversePrimary,
    surfaceTint = DesignColors.surfaceTint,
)

private val DarkColorScheme = darkColorScheme(
    primary = DesignColorsDark.primary,
    onPrimary = DesignColorsDark.onPrimary,
    primaryContainer = DesignColorsDark.primaryContainer,
    onPrimaryContainer = DesignColorsDark.onPrimaryContainer,
    secondary = DesignColorsDark.secondary,
    onSecondary = DesignColorsDark.onSecondary,
    secondaryContainer = DesignColorsDark.secondaryContainer,
    onSecondaryContainer = DesignColorsDark.onSecondaryContainer,
    tertiary = DesignColorsDark.tertiary,
    onTertiary = DesignColorsDark.onTertiary,
    tertiaryContainer = DesignColorsDark.tertiaryContainer,
    onTertiaryContainer = DesignColorsDark.onTertiaryContainer,
    error = DesignColorsDark.error,
    onError = DesignColorsDark.onError,
    errorContainer = DesignColorsDark.errorContainer,
    onErrorContainer = DesignColorsDark.onErrorContainer,
    background = DesignColorsDark.background,
    onBackground = DesignColorsDark.onBackground,
    surface = DesignColorsDark.surface,
    onSurface = DesignColorsDark.onSurface,
    surfaceVariant = DesignColorsDark.surfaceVariant,
    onSurfaceVariant = DesignColorsDark.onSurfaceVariant,
    surfaceBright = DesignColorsDark.surfaceBright,
    surfaceDim = DesignColorsDark.surfaceDim,
    surfaceContainer = DesignColorsDark.surfaceContainer,
    surfaceContainerLow = DesignColorsDark.surfaceContainerLow,
    surfaceContainerHigh = DesignColorsDark.surfaceContainerHigh,
    surfaceContainerHighest = DesignColorsDark.surfaceContainerHighest,
    outline = DesignColorsDark.outline,
    outlineVariant = DesignColorsDark.outlineVariant,
    scrim = DesignColorsDark.scrim,
    inverseSurface = DesignColorsDark.inverseSurface,
    inverseOnSurface = DesignColorsDark.inverseOnSurface,
    inversePrimary = DesignColorsDark.inversePrimary,
    surfaceTint = DesignColorsDark.surfaceTint,
)

@Composable
fun FriendsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
