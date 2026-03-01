package au.edu.jcu.fittrackplus.ui.i18n

data class AppStrings(
    val isZh: Boolean,

    val appName: String,

    // bottom tab
    val home: String,
    val setting: String,

    // common
    val back: String,
    val reset: String,
    val delete: String,
    val save: String,
    val exit: String,
    val ok: String,
    val cancel: String,
    val selectAll: String,
    val new: String,
    val edit: String,

    // ✅ new: used by schedule list button description
    val detail: String,

    // common fields/labels
    val name: String,
    val gender: String,
    val male: String,
    val female: String,
    val other: String,
    val age: String,
    val heightCm: String,
    val weightKg: String,
    val type: String,
    val category: String,
    val date: String,
    val time: String,
    val noteLabel: String,
    val startLabel: String,

    // home
    val quickStart: String,
    val pause: String,
    val start: String,
    val stop: String,
    val schedule: String,
    val history: String,

    // i18n: Hour/Min label
    val hour: String,
    val minute: String,

    val targetTimeOptional: String,
    val targetMinOptional: String,

    val savedToHistory: String,
    val timeUp: String,

    val exitWorkoutTitle: String,
    val exitWorkoutBody: String,
    val workoutTypesHintTitle: String,
    val workoutTypesHintBody: String,

    // history
    val historyTitle: String,
    val noRecords: String,
    val selected: String,

    // settings
    val settingsTitle: String,
    val personalInfo: String,
    val workoutTypeManagement: String,
    val preferences: String,
    val personalInfoTitle: String,

    // prefs
    val preferencesTitle: String,
    val language: String,
    val theme: String,
    val light: String,
    val dark: String,

    // workout types manage
    val workoutTypesTitle: String,
    val filter: String,
    val all: String,
    val applied: String,
    val notApplied: String,

    // 这个 apply 复用：既可以表示“应用运动种类”，也可以表示“应用计划”
    val apply: String,
    val cancelApply: String,

    // schedule (plans)
    val scheduleTitle: String,
    val newPlanTitle: String,
    val planDetailTitle: String,
    val planName: String,
    val durationMinutes: String,
    val estimatedCalories: String,
    val planNote: String,
    val saveChanges: String
)

object StringsFactory {

    fun of(lang: AppLanguage): AppStrings {
        return when (lang) {
            AppLanguage.EN -> en()
            AppLanguage.ZH -> zh()
        }
    }

    private fun en() = AppStrings(
        isZh = false,

        appName = "FitTrack+",

        home = "Home",
        setting = "Setting",

        back = "Back",
        reset = "Reset",
        delete = "Delete",
        save = "Save",
        exit = "Exit",
        ok = "OK",
        cancel = "Cancel",
        selectAll = "Select All",
        new = "New",
        edit = "Edit",

        detail = "Detail",

        name = "Name",
        gender = "Gender",
        male = "male",
        female = "female",
        other = "other",
        age = "Age",
        heightCm = "Height (cm)",
        weightKg = "Weight (kg)",
        type = "Type",
        category = "Category",
        date = "Date",
        time = "Time",
        noteLabel = "Note",
        startLabel = "Start",

        quickStart = "Quick\nStart",
        pause = "Pause",
        start = "Start",
        stop = "Stop",
        schedule = "Schedule",
        history = "History",

        hour = "Hour",
        minute = "Min",

        targetTimeOptional = "Target time (HH:mm) (optional)",
        targetMinOptional = "Target min (optional)",

        savedToHistory = "Saved to history.",
        timeUp = "Time is up.",

        exitWorkoutTitle = "Exit workout?",
        exitWorkoutBody = "If you exit now, this workout will NOT be saved.",
        workoutTypesHintTitle = "Workout Types",
        workoutTypesHintBody = "Workout types can be managed in Settings → Workout Type Management.",

        historyTitle = "History",
        noRecords = "No records found.",
        selected = "Selected",

        settingsTitle = "Settings",
        personalInfo = "Personal Info",
        workoutTypeManagement = "Workout Type Management",
        preferences = "Preferences",
        personalInfoTitle = "Personal Info",

        preferencesTitle = "Preferences",
        language = "Language",
        theme = "Theme",
        light = "Light",
        dark = "Dark",

        workoutTypesTitle = "Workout Types",
        filter = "Filter",
        all = "All",
        applied = "Applied",
        notApplied = "Not applied",
        apply = "Apply",
        cancelApply = "Cancel Apply",

        scheduleTitle = "Schedule",
        newPlanTitle = "New Plan",
        planDetailTitle = "Plan Detail",
        planName = "Plan Name",
        durationMinutes = "Duration (minutes)",
        estimatedCalories = "Estimated Calories",
        planNote = "Plan Note",
        saveChanges = "Save Changes"
    )

    private fun zh() = AppStrings(
        isZh = true,

        appName = "FitTrack+",

        home = "首页",
        setting = "设置",

        back = "返回",
        reset = "重置",
        delete = "删除",
        save = "保存",
        exit = "退出",
        ok = "确定",
        cancel = "取消",
        selectAll = "选择全部",
        new = "新建",
        edit = "编辑",

        detail = "详情",

        name = "姓名",
        gender = "性别",
        male = "男",
        female = "女",
        other = "其他",
        age = "年龄",
        heightCm = "身高（cm）",
        weightKg = "体重（kg）",
        type = "种类",
        category = "运动种类",
        date = "日期",
        time = "时间",
        noteLabel = "备注",
        startLabel = "开始",

        quickStart = "快速\n开始",
        pause = "暂停",
        start = "开始",
        stop = "停止",
        schedule = "计划",
        history = "历史",

        hour = "小时",
        minute = "分钟",

        targetTimeOptional = "目标时间（HH:mm，可选）",
        targetMinOptional = "目标分钟（可选）",

        savedToHistory = "已保存到历史记录。",
        timeUp = "时间到。",

        exitWorkoutTitle = "退出运动？",
        exitWorkoutBody = "现在退出将不会保存本次运动记录。",
        workoutTypesHintTitle = "运动种类",
        workoutTypesHintBody = "运动种类可在 设置 → 运动种类管理 中配置。",

        historyTitle = "历史记录",
        noRecords = "没有符合条件的记录。",
        selected = "已选中",

        settingsTitle = "设置",
        personalInfo = "个人信息",
        workoutTypeManagement = "运动种类管理",
        preferences = "偏好设置",
        personalInfoTitle = "个人信息",

        preferencesTitle = "偏好设置",
        language = "语言",
        theme = "主题",
        light = "浅色",
        dark = "深色",

        workoutTypesTitle = "运动种类",
        filter = "筛选",
        all = "全部",
        applied = "已应用",
        notApplied = "未应用",
        apply = "应用",
        cancelApply = "取消应用",

        scheduleTitle = "计划",
        newPlanTitle = "新建计划",
        planDetailTitle = "计划详情",
        planName = "计划名称",
        durationMinutes = "运动时长（分钟）",
        estimatedCalories = "预估消耗（千卡）",
        planNote = "计划备注",
        saveChanges = "保存修改"
    )
}