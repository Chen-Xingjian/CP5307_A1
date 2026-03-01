### Utility App Starter – CP3406 / CP5307

This is a basic Android app template for **Assessment 1: Utility App** in CP3406/CP5603..  
It provides the structure for a simple tabular UI using **Jetpack Compose** and **Material Design 3**.

---

## Getting Started

### How to Run
1. Clone or download this repo  
2. Open in Android Studio  
3. Run on an emulator or physical device (API 26+ recommended)  

---

## Composables

### UtilityApp()
- Contains the screen layout using a Scaffold
- Toggles content between Utility and Settings

### UtilityScreen()
- Displays a simple counter (replace with your utility logic)  
- Includes a button to increment the counter

### SettingsScreen()
- Placeholder for user preferences or configuration  
- Can be extended to modify main screen behavior (e.g., theme, units, limits)  

---

## Key Concepts Covered

| Week | Concept                        | Used In                          |
|------|--------------------------------|----------------------------------|
| 1    | Kotlin + Android Studio         | MainActivity.kt |
| 2    | Jetpack Compose Layouts         | UtilityApp(), UtilityScreen(), SettingsScreen()   |
| 3    | Material Design 3               | CP3406_CP5603UtilityAppStarterTemplateTheme, MaterialTheme.typography |
| 4    | ViewModel | Not included in starter          |
| 5    | Retrofit  | Not included in starter          |

---

## Suggested Extensions
- Replace counter with a real utility (e.g., hydration tracker, timer)  
- Add a ViewModel for state management  
- Use SharedPreferences or DataStore to persist settings  
- Add a simple API call using Retrofit (e.g., fetch weather or quotes)  

---

## 📚 License
This template is provided for educational use in CP3406.  
Feel free to modify and extend it for your assessment.
**# FitTrack+ (CP3406 Utility App)

FitTrack+ is a lightweight fitness utility app built with **Android (Kotlin)** and **Jetpack Compose**.  
It helps users quickly start a workout timer, track workout history, manage workout plans, and customize workout types, language, and theme.

---

## Features

### ✅ Workout Timer (Home)
- Quick Start workout session
- Countdown or stopwatch mode (based on target time input)
- Pause / Resume / Stop
- Save workout record to history
- Exit confirmation to prevent accidental loss

### ✅ Workout History
- View saved workout sessions
- Filter by **Workout Type** and **Date**
- Multi-select and delete records

### ✅ Schedule (Workout Plans)
- Create workout plans (type, duration, estimated calories, notes)
- View plan list with actions:
  - **Detail**: view/edit plan details
  - **Delete**: remove a plan
  - **Apply**: start a workout using plan presets
- “Saved to history” feedback displayed on the Schedule screen after finishing a plan-based workout

### ✅ Workout Type Management
- Enable/disable workout types available in the app
- Filter types by:
  - All
  - Applied
  - Not applied

### ✅ Settings
- Personal Profile:
  - Name, gender, age, height, weight
  - Used for calorie estimation
- Preferences:
  - Language: **English / 中文**
  - Theme: **Light / Dark**

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM (ViewModel + StateFlow)
- **Persistence:** Room Database
- **Dependency Injection:** Hilt
- **Reactive Streams:** Kotlin Flow / StateFlow

---

## Project Structure (High Level)

- `data/`  
  Room entities, DAOs, database, repository implementation

- `domain/`  
  Models, repository interface, utilities (e.g., calorie calculator)

- `ui/`  
  Compose screens, reusable UI components, navigation graph, theme, i18n, ViewModels

- `di/`  
  Hilt modules for Room and repository binding

---

## How to Run

1. Open the project in **Android Studio (latest stable recommended)**.
2. Sync Gradle.
3. Run on an emulator or a physical Android device.

---

## Notes

- Workout type availability is controlled in **Settings → Workout Type Management**.
- Language and theme settings are persisted via Room preferences.
- Plan-based workouts return to the **Schedule** screen after saving, and the confirmation snackbar is shown there.

---

## Screens Overview

- **Home:** workout timer + quick start  
- **History:** workout records with filters and deletion  
- **Schedule:** manage workout plans and apply presets  
- **Settings:** profile + workout type management + preferences  

---

## Author

Chen Xingjian  
James Cook University Singapore (JCU)  
CP3406 Assignment 1 – Utility App
