package au.edu.jcu.fittrackplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.fittrackplus.domain.util.collectAsStateWithLifecycleCompat
import au.edu.jcu.fittrackplus.ui.i18n.AppLanguage
import au.edu.jcu.fittrackplus.ui.i18n.AppLocaleViewModel
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.StringsFactory
import au.edu.jcu.fittrackplus.ui.navigation.FitTrackNavGraph
import au.edu.jcu.fittrackplus.ui.theme.AppThemeViewModel
import au.edu.jcu.fittrackplus.ui.theme.FitTrackTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity hosting the Jetpack Compose UI.
 *
 * Responsibilities:
 * - Observes user preferences (theme and language) via ViewModels.
 * - Provides the localized string set via [LocalStrings].
 * - Applies the app theme via [FitTrackTheme].
 * - Hosts the root navigation graph via [FitTrackNavGraph].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Theme preference (LIGHT/DARK) exposed as a Boolean state.
            val themeVm: AppThemeViewModel = viewModel()
            val isDark by themeVm.darkTheme.collectAsStateWithLifecycleCompat(false)

            // Language preference (EN/ZH) used to build the current string bundle.
            val localeVm: AppLocaleViewModel = viewModel()
            val lang by localeVm.language.collectAsStateWithLifecycleCompat(AppLanguage.EN)

            // Resolve the localized strings for the current language.
            val strings = StringsFactory.of(lang)

            FitTrackTheme(darkTheme = isDark) {
                // Provide strings to all composables in the subtree.
                CompositionLocalProvider(LocalStrings provides strings) {
                    FitTrackNavGraph()
                }
            }
        }
    }
}