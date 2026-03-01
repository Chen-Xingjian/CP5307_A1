package au.edu.jcu.fittrackplus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography configuration for FitTrack+.
 *
 * This file defines a consistent text scale across the app using Material 3 typography slots.
 * If you do not ship a custom font, the system default font family is a solid choice.
 */
private val AppFont = FontFamily.Default

/**
 * App-wide typography tokens mapped to Material 3 text styles.
 *
 * Notes:
 * - Headline styles are used for prominent page titles and large emphasis text.
 * - Title styles are used for section headers and card titles.
 * - Body styles are used for general content, labels, and supporting text.
 * - Label styles are used for buttons, chips, and compact UI labels.
 */
val FitTrackTypography = Typography(
    /**
     * Large page title for top-level screens (e.g., "Home", "Schedule", "History").
     */
    headlineSmall = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 30.sp
    ),

    /**
     * Very prominent headline, typically for timer display and key metrics.
     */
    headlineMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 34.sp
    ),

    /**
     * Section title used for grouping content (e.g., "Filters", "Plan Detail").
     */
    titleLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),

    /**
     * Card title / list item title (e.g., plan name, workout type name).
     */
    titleMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),

    /**
     * Primary body text for paragraphs or important descriptions.
     */
    bodyLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),

    /**
     * Default body text for most UI copy and supporting information.
     */
    bodyMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    /**
     * Small supporting text (e.g., helper text, secondary metadata).
     */
    bodySmall = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    /**
     * Prominent label style (e.g., primary buttons).
     */
    labelLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),

    /**
     * Compact label style (e.g., small badges, selection indicators).
     */
    labelMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)