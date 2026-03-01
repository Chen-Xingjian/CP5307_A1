package au.edu.jcu.fittrackplus.ui.i18n

enum class AppLanguage(val code: String) {
    EN("EN"),
    ZH("ZH");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return when (code?.uppercase()) {
                "ZH" -> ZH
                else -> EN
            }
        }
    }
}