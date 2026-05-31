package friends.mobile.designkit.theme

import androidx.compose.ui.graphics.Color

object DesignColors {

    // Brand — iOS system blue #007AFF
    val primary = Color(0xFF007AFF)
    val primaryHex = Color(red = 0.0f, green = 0.48f, blue = 1.0f)
    val onPrimary = Color.White
    val primaryContainer = Color(0xFFD0E4FF)
    val onPrimaryContainer = Color(0xFF001D36)
    val inversePrimary = Color(0xFF8FBFFF)

    // Secondary — iOS systemGray #8E8E93
    val secondary = Color(0xFF8E8E93)
    val onSecondary = Color.White
    val secondaryContainer = Color(0xFFE5E5EA)   // systemGray5
    val onSecondaryContainer = Color(0xFF1C1C1E) // label

    // Tertiary — iOS green #34C759
    val tertiary = Color(0xFF34C759)
    val onTertiary = Color.White
    val tertiaryContainer = Color(0xFFD4F5DD)
    val onTertiaryContainer = Color(0xFF002112)

    // Error — iOS system red #FF3B30
    val error = Color(0xFFFF3B30)
    val onError = Color.White
    val errorContainer = Color(0xFFFFEDEC)
    val onErrorContainer = Color(0xFF410002)
    val errorLight = Color.Red.copy(alpha = 0.08f)

    // Background & Surface — systemBackground #FFFFFF
    val background = Color.White
    val onBackground = Color(0xFF1C1C1E)      // label
    val surface = Color.White
    val onSurface = Color(0xFF1C1C1E)         // label
    val surfaceVariant = Color(0xFFF2F2F7)    // systemGray6
    val onSurfaceVariant = Color(0xFF8E8E93)  // systemGray
    val surfaceBright = Color.White
    val surfaceDim = Color(0xFFEBEBF0)
    val surfaceContainer = Color(0xFFF2F2F7)        // systemGray6 — NavigationBar bg
    val surfaceContainerLow = Color.White            // ModalBottomSheet bg
    val surfaceContainerHigh = Color(0xFFEBEBF0)     // DatePicker dialog bg
    val surfaceContainerHighest = Color(0xFFE5E5EA)  // systemGray5 — TimePicker clock face
    val inverseSurface = Color(0xFF1C1C1E)
    val inverseOnSurface = Color(0xFFF2F2F7)
    val surfaceTint = Color.Transparent

    // Outline — systemGray3 / systemGray4
    val outline = Color(0xFFC7C7CC)        // systemGray3
    val outlineVariant = Color(0xFFD1D1D6) // systemGray4
    val scrim = Color.Black

    // App-specific tokens
    val textField = Color(0xFFF2F2F7)       // text field bg (= systemGray6)
    val infoLight = Color(red = 0.0f, green = 0.48f, blue = 1.0f).copy(alpha = 0.06f)
    val secondaryAccent = Color(0xFF34C759) // = tertiary — busy day indicator
    val systemGray6 = Color(0xFFF2F2F7)
}

object DesignColorsDark {

    // Brand — iOS dark system blue #0A84FF
    val primary = Color(0xFF0A84FF)
    val primaryHex = Color(0xFF0A84FF)
    val onPrimary = Color(0xFF003566)
    val primaryContainer = Color(0xFF004789)
    val onPrimaryContainer = Color(0xFFD6E3FF)
    val inversePrimary = Color(0xFF0A84FF)

    // Secondary — iOS dark systemGray2 #636366
    val secondary = Color(0xFF636366)
    val onSecondary = Color.White
    val secondaryContainer = Color(0xFF3A3A3C) // systemGray4 dark
    val onSecondaryContainer = Color(0xFFEBEBF5)

    // Tertiary — iOS dark green #30D158
    val tertiary = Color(0xFF30D158)
    val onTertiary = Color.Black
    val tertiaryContainer = Color(0xFF004D13)
    val onTertiaryContainer = Color(0xFFC3F0C8)

    // Error — iOS dark system red #FF453A
    val error = Color(0xFFFF453A)
    val onError = Color.Black
    val errorContainer = Color(0xFF93000A)
    val onErrorContainer = Color(0xFFFFDAD6)
    val errorLight = Color(0xFFFF453A).copy(alpha = 0.12f)

    // Background & Surface — iOS dark systemBackground #000000
    val background = Color(0xFF000000)      // systemBackground dark
    val onBackground = Color.White          // label dark
    val surface = Color(0xFF000000)
    val onSurface = Color.White
    val surfaceVariant = Color(0xFF1C1C1E)  // systemGray6 dark
    val onSurfaceVariant = Color(0xFF8E8E93)
    val surfaceBright = Color(0xFF2C2C2E)   // systemGray5 dark
    val surfaceDim = Color(0xFF000000)
    val surfaceContainer = Color(0xFF1C1C1E)        // systemGray6 dark — NavigationBar bg
    val surfaceContainerLow = Color(0xFF000000)      // ModalBottomSheet bg
    val surfaceContainerHigh = Color(0xFF2C2C2E)     // systemGray5 dark — DatePicker bg
    val surfaceContainerHighest = Color(0xFF3A3A3C)  // systemGray4 dark — TimePicker clock
    val inverseSurface = Color.White
    val inverseOnSurface = Color(0xFF1C1C1E)
    val surfaceTint = Color.Transparent

    // Outline — iOS dark systemGray3 / systemGray4
    val outline = Color(0xFF48484A)        // systemGray3 dark
    val outlineVariant = Color(0xFF3A3A3C) // systemGray4 dark
    val scrim = Color.Black
}
