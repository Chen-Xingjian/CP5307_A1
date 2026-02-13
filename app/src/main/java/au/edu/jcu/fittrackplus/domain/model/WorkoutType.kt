package au.edu.jcu.fittrackplus.domain.model

enum class WorkoutType(val displayName: String) {
    RUNNING("Running"),
    CYCLING("Cycling"),
    SWIMMING("Swimming"),
    WALKING("Walking"),
    STRENGTH("Strength Training"),
    YOGA("Yoga");

    companion object {
        fun fromName(name: String): WorkoutType =
            entries.firstOrNull { it.name == name || it.displayName == name } ?: RUNNING
    }
}