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

// ---------------------------------------------------------------------------------------------
// Material 3 Color Schemes
// ---------------------------------------------------------------------------------------------
// These schemes map the app's design tokens (defined in Color.kt) into Material 3 semantic slots.
// The rest of the UI should only depend on MaterialTheme.colorScheme for consistent styling.

/**
 * Light theme color scheme for FitTrack+.
 *
 * Uses brand greens for primary/secondary, plus custom surface/background/outline tokens to create
 * a "clean fitness app" look: light background, bright surfaces, and strong outlines for inputs.
 */
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

/**
 * Dark theme color scheme for FitTrack+.
 *
 * Dark theme uses adjusted greens for better contrast and readability on darker surfaces.
 * Surface and outline tokens are also tuned to avoid low-contrast borders and text.
 */
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

// ---------------------------------------------------------------------------------------------
// Shapes
// ---------------------------------------------------------------------------------------------
// Shapes are part of the MaterialTheme and affect default rounding for components.
// These values align with a modern "card-first" fitness app aesthetic (soft, rounded corners).

/**
 * Shared shape system for FitTrack+ UI components.
 *
 * - Smaller shapes are used for inputs and small controls.
 * - Larger shapes are used for cards and prominent containers.
 */
private val FitTrackShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// ---------------------------------------------------------------------------------------------
// Theme wrapper
// ---------------------------------------------------------------------------------------------

/**
 * Top-level theme entry for FitTrack+.
 *
 * This function wires together:
 * - Color scheme (light/dark)
 * - Typography (FitTrackTypography)
 * - Shapes (FitTrackShapes)
 *
 * All screens should be composed under this theme to ensure consistent UI styling.
 */
@Composable
fun FitTrackTheme(
    // Defaults to the user's system setting unless overridden by the app preference.
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Select the active scheme based on the requested mode.
    val scheme = if (darkTheme) DarkScheme else LightScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = FitTrackTypography,
        shapes = FitTrackShapes,
        content = content
    )
}