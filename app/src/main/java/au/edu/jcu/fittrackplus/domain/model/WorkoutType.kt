package au.edu.jcu.fittrackplus.domain.model

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
        fun fromName(name: String): WorkoutType =
            entries.firstOrNull { it.name == name } ?: RUNNING
    }
}