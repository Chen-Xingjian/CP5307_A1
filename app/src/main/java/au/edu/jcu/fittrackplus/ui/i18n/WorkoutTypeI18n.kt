package au.edu.jcu.fittrackplus.ui.i18n

import au.edu.jcu.fittrackplus.domain.model.WorkoutType

/**
 * Returns a localized display name for a [WorkoutType].
 *
 * Notes:
 * - English uses [WorkoutType.displayName] directly.
 * - Chinese uses a fixed mapping to keep UI labels consistent across screens.
 * - This function is UI-layer only and does not affect stored values (which use `WorkoutType.name`).
 *
 * @param isZh Whether the current UI language is Chinese.
 */
fun WorkoutType.localizedName(isZh: Boolean): String {
    // Use the built-in English display name when not in Chinese.
    if (!isZh) return this.displayName

    // Chinese label mapping (UI only).
    return when (this) {
        WorkoutType.RUNNING -> "跑步"
        WorkoutType.WALKING -> "步行"
        WorkoutType.CYCLING -> "骑行"
        WorkoutType.SWIMMING -> "游泳"
        WorkoutType.STRENGTH -> "力量训练"
        WorkoutType.YOGA -> "瑜伽"
        WorkoutType.HIIT -> "高强度间歇"
        WorkoutType.PILATES -> "普拉提"
        WorkoutType.ROWING -> "划船机"
        WorkoutType.HIKING -> "徒步"
        WorkoutType.ELLIPTICAL -> "椭圆机"
        WorkoutType.STAIR_CLIMBING -> "爬楼/登阶"
        WorkoutType.JUMP_ROPE -> "跳绳"
        WorkoutType.BOXING -> "拳击"
        WorkoutType.BADMINTON -> "羽毛球"
        WorkoutType.BASKETBALL -> "篮球"
        WorkoutType.FOOTBALL -> "足球"
        WorkoutType.TENNIS -> "网球"
        WorkoutType.TABLE_TENNIS -> "乒乓球"
        WorkoutType.VOLLEYBALL -> "排球"
        WorkoutType.DANCE -> "舞蹈"
        WorkoutType.SKIPPING -> "跳跃训练"
        WorkoutType.SKATING -> "滑冰/轮滑"
        WorkoutType.CLIMBING -> "攀岩"
        WorkoutType.MARTIAL_ARTS -> "武术/搏击"
    }
}