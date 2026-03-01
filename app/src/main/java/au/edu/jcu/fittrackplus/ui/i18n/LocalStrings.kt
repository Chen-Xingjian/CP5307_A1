package au.edu.jcu.fittrackplus.ui.i18n

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal that provides the current [AppStrings] instance to the Compose UI tree.
 *
 * Usage:
 * - Wrap your app content with `CompositionLocalProvider(LocalStrings provides strings)`.
 * - Read strings inside composables via `val s = LocalStrings.current`.
 *
 * The default factory throws to fail fast if the provider is missing.
 */
val LocalStrings = compositionLocalOf<AppStrings> {
    error("LocalStrings not provided")
}