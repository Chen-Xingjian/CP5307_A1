package au.edu.jcu.fittrackplus.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------------------------
// Brand colors
// ---------------------------------------------------------------------------------------------
// Note: These colors are treated as design tokens for the app's identity. They are referenced
// by the Material 3 color scheme defined in Theme.kt.

/** Primary brand green used for main actions and highlights (light theme). */
val PrimaryGreen = Color(0xFF2E7D32)

/** Foreground color to be used on top of [PrimaryGreen] (light theme). */
val OnPrimaryGreen = Color(0xFFFFFFFF)

/** Softer primary container color for surfaces such as tonal buttons and chips (light theme). */
val PrimaryGreenContainer = Color(0xFFA5D6A7)

/** Foreground color to be used on top of [PrimaryGreenContainer] (light theme). */
val OnPrimaryGreenContainer = Color(0xFF0E3B14)

/** Secondary green used for supporting accents (light theme). */
val SecondaryGreen = Color(0xFF1B5E20)

/** Foreground color to be used on top of [SecondaryGreen] (light theme). */
val OnSecondaryGreen = Color(0xFFFFFFFF)

/** Secondary container color for subtle emphasis (light theme). */
val SecondaryGreenContainer = Color(0xFFC8E6C9)

/** Foreground color to be used on top of [SecondaryGreenContainer] (light theme). */
val OnSecondaryGreenContainer = Color(0xFF0B2A10)

// ---------------------------------------------------------------------------------------------
// Dark theme variants
// ---------------------------------------------------------------------------------------------
// Note: Dark variants are tuned for contrast on dark surfaces (slightly brighter greens).

/** Primary brand green adjusted for readability on dark backgrounds (dark theme). */
val PrimaryGreenDark = Color(0xFF66BB6A)

/** Foreground color to be used on top of [PrimaryGreenDark] (dark theme). */
val OnPrimaryGreenDark = Color(0xFF081A0B)

/** Primary container color for dark theme surfaces (dark theme). */
val PrimaryGreenContainerDark = Color(0xFF1E3A22)

/** Foreground color to be used on top of [PrimaryGreenContainerDark] (dark theme). */
val OnPrimaryGreenContainerDark = Color(0xFFDDF6DF)

/** Secondary green adjusted for dark backgrounds (dark theme). */
val SecondaryGreenDark = Color(0xFF81C784)

/** Foreground color to be used on top of [SecondaryGreenDark] (dark theme). */
val OnSecondaryGreenDark = Color(0xFF081A0B)

/** Secondary container color for dark theme surfaces (dark theme). */
val SecondaryGreenContainerDark = Color(0xFF203A25)

/** Foreground color to be used on top of [SecondaryGreenContainerDark] (dark theme). */
val OnSecondaryGreenContainerDark = Color(0xFFDDF6DF)

// ---------------------------------------------------------------------------------------------
// App surfaces (Light)
// ---------------------------------------------------------------------------------------------
// Note: These tokens shape the overall “fitness app” look (clean background + elevated cards).

/** App background color (light theme). */
val AppBackgroundLight = Color(0xFFF7F7FA)

/** Default text/icon color displayed on [AppBackgroundLight]. */
val AppOnBackgroundLight = Color(0xFF101318)

/** Primary surface color for cards and sheets (light theme). */
val AppSurfaceLight = Color(0xFFFFFFFF)

/** Default text/icon color displayed on [AppSurfaceLight]. */
val AppOnSurfaceLight = Color(0xFF101318)

/** Secondary surface color for subtle containers and separated sections (light theme). */
val AppSurfaceVariantLight = Color(0xFFEDEEF2)

/** Default text/icon color displayed on [AppSurfaceVariantLight]. */
val AppOnSurfaceVariantLight = Color(0xFF2B2F36)

/** Strong outline used for key borders (e.g., outlined text fields) in light theme. */
val AppOutlineLight = Color(0xFF20242B)

/** Subtle outline used for dividers and less prominent borders (light theme). */
val AppOutlineVariantLight = Color(0xFFC9CDD6)

// ---------------------------------------------------------------------------------------------
// App surfaces (Dark)
// ---------------------------------------------------------------------------------------------

/** App background color (dark theme). */
val AppBackgroundDark = Color(0xFF0E1116)

/** Default text/icon color displayed on [AppBackgroundDark]. */
val AppOnBackgroundDark = Color(0xFFE6EAF2)

/** Primary surface color for cards and sheets (dark theme). */
val AppSurfaceDark = Color(0xFF121723)

/** Default text/icon color displayed on [AppSurfaceDark]. */
val AppOnSurfaceDark = Color(0xFFE6EAF2)

/** Secondary surface color for containers and grouped sections (dark theme). */
val AppSurfaceVariantDark = Color(0xFF1A2230)

/** Default text/icon color displayed on [AppSurfaceVariantDark]. */
val AppOnSurfaceVariantDark = Color(0xFFCAD2E2)

/** Outline color for borders in dark theme. */
val AppOutlineDark = Color(0xFF8F9BB3)

/** Subtle outline for dividers and low-emphasis borders in dark theme. */
val AppOutlineVariantDark = Color(0xFF2C364A)

// ---------------------------------------------------------------------------------------------
// Error colors
// ---------------------------------------------------------------------------------------------
// Note: These match Material 3 error semantics and are used for validation and destructive states.

val AppErrorLight = Color(0xFFB3261E)
val AppOnErrorLight = Color(0xFFFFFFFF)
val AppErrorContainerLight = Color(0xFFF9DEDC)
val AppOnErrorContainerLight = Color(0xFF410E0B)

val AppErrorDark = Color(0xFFF2B8B5)
val AppOnErrorDark = Color(0xFF601410)
val AppErrorContainerDark = Color(0xFF8C1D18)
val AppOnErrorContainerDark = Color(0xFFF9DEDC)