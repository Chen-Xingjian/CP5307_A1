package au.edu.jcu.fittrackplus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point for FitTrack+.
 *
 * This enables Hilt dependency injection for the entire app process.
 * Hilt will generate the required components starting from this Application class.
 */
@HiltAndroidApp
class FitTrackApp : Application()