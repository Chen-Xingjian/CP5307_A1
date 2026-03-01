package au.edu.jcu.fittrackplus.domain.model

/**
 * Domain model representing user app-level preferences.
 *
 * Notes:
 * - language uses stable codes (e.g., "EN", "ZH") for persistence and logic.
 * - theme uses stable codes (e.g., "LIGHT", "DARK") for persistence and logic.
 *
 * @property language Language code for localization ("EN" / "ZH").
 * @property theme Theme code for UI appearance ("LIGHT" / "DARK").
 */
data class AppPreferences(
    val language: String = "EN",   // "EN" / "ZH"
    val theme: String = "LIGHT"    // "LIGHT" / "DARK"
)