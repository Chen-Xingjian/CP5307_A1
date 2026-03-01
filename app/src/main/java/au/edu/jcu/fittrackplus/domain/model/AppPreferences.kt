package au.edu.jcu.fittrackplus.domain.model

data class AppPreferences(
    val language: String = "EN",   // "EN" / "ZH"
    val theme: String = "LIGHT"    // "LIGHT" / "DARK"
)