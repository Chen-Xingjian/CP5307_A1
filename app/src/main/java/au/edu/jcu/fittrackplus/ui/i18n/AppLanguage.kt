package au.edu.jcu.fittrackplus.ui.i18n

/**
 * Supported application languages.
 *
 * The [code] value is persisted in preferences and used to restore the user's choice
 * across app launches.
 */
enum class AppLanguage(val code: String) {
    /** English (default). */
    EN("EN"),

    /** Simplified Chinese. */
    ZH("ZH");

    companion object {

        /**
         * Converts a persisted language code to an [AppLanguage].
         *
         * Any unknown or null value falls back to [EN] to keep the app functional.
         *
         * @param code Persisted language code (e.g., "EN", "ZH").
         * @return The matching [AppLanguage], or [EN] if not recognized.
         */
        fun fromCode(code: String?): AppLanguage {
            return when (code?.uppercase()) {
                "ZH" -> ZH
                else -> EN
            }
        }
    }
}