# FitTrack+ (CP3406 Utility App)

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
