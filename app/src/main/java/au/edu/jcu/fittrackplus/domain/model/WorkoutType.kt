package au.edu.jcu.fittrackplus.domain.model

/**
 * Supported workout types in the app.
 *
 * Notes:
 * - [name] is the stable identifier persisted in storage (e.g., Room) and should not be changed.
 * - [displayName] is a user-facing label (currently English). It can be replaced by i18n strings
 *   in [AppStrings] to avoid duplication and keep localization in one place.
 */
enum class WorkoutType(val displayName: String) {
    RUNNING("Running"),
    WALKING("Walking"),
    CYCLING("Cycling"),
    SWIMMING("Swimming"),
    STRENGTH("Strength Training"),
    YOGA("Yoga"),
    HIIT("HIIT"),
    PILATES("Pilates"),
    ROWING("Rowing"),
    HIKING("Hiking"),
    ELLIPTICAL("Elliptical"),
    STAIR_CLIMBING("Stair Climbing"),
    JUMP_ROPE("Jump Rope"),
    BOXING("Boxing"),
    BADMINTON("Badminton"),
    BASKETBALL("Basketball"),
    FOOTBALL("Football/Soccer"),
    TENNIS("Tennis"),
    TABLE_TENNIS("Table Tennis"),
    VOLLEYBALL("Volleyball"),
    DANCE("Dance"),
    SKIPPING("Skipping"),
    SKATING("Skating"),
    CLIMBING("Climbing"),
    MARTIAL_ARTS("Martial Arts");

    companion object {
        /**
         * Parses a stable enum [name] into [WorkoutType].
         *
         * This is used when reading persisted values. If the input is unknown, it falls back
         * to [RUNNING] to keep the app resilient to malformed or legacy data.
         *
         * @param name Enum name persisted in storage (e.g., "RUNNING").
         * @return Matching [WorkoutType] or [RUNNING] if not found.
         */
        fun fromName(name: String): WorkoutType =
            entries.firstOrNull { it.name == name } ?: RUNNING
    }
}