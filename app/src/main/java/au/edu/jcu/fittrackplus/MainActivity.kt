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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeVm: AppThemeViewModel = viewModel()
            val isDark by themeVm.darkTheme.collectAsStateWithLifecycleCompat(false)

            val localeVm: AppLocaleViewModel = viewModel()
            val lang by localeVm.language.collectAsStateWithLifecycleCompat(AppLanguage.EN)

            val strings = StringsFactory.of(lang)

            FitTrackTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalStrings provides strings) {
                    FitTrackNavGraph()
                }
            }
        }
    }
}