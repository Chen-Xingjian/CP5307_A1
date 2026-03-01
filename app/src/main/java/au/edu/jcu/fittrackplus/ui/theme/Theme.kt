package au.edu.jcu.fittrackplus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryGreen,
    primaryContainer = PrimaryGreenContainer,
    onPrimaryContainer = OnPrimaryGreenContainer,

    secondary = SecondaryGreen,
    onSecondary = OnSecondaryGreen,
    secondaryContainer = SecondaryGreenContainer,
    onSecondaryContainer = OnSecondaryGreenContainer,

    background = AppBackgroundLight,
    onBackground = AppOnBackgroundLight,

    surface = AppSurfaceLight,
    onSurface = AppOnSurfaceLight,
    surfaceVariant = AppSurfaceVariantLight,
    onSurfaceVariant = AppOnSurfaceVariantLight,

    outline = AppOutlineLight,
    outlineVariant = AppOutlineVariantLight,

    error = AppErrorLight,
    onError = AppOnErrorLight,
    errorContainer = AppErrorContainerLight,
    onErrorContainer = AppOnErrorContainerLight
)

private val DarkScheme = darkColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = OnPrimaryGreenDark,
    primaryContainer = PrimaryGreenContainerDark,
    onPrimaryContainer = OnPrimaryGreenContainerDark,

    secondary = SecondaryGreenDark,
    onSecondary = OnSecondaryGreenDark,
    secondaryContainer = SecondaryGreenContainerDark,
    onSecondaryContainer = OnSecondaryGreenContainerDark,

    background = AppBackgroundDark,
    onBackground = AppOnBackgroundDark,

    surface = AppSurfaceDark,
    onSurface = AppOnSurfaceDark,
    surfaceVariant = AppSurfaceVariantDark,
    onSurfaceVariant = AppOnSurfaceVariantDark,

    outline = AppOutlineDark,
    outlineVariant = AppOutlineVariantDark,

    error = AppErrorDark,
    onError = AppOnErrorDark,
    errorContainer = AppErrorContainerDark,
    onErrorContainer = AppOnErrorContainerDark
)

private val FitTrackShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun FitTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkScheme else LightScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = FitTrackTypography,
        shapes = FitTrackShapes,
        content = content
    )
}