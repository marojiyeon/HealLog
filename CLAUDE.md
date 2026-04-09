# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.heallog.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

## Project Structure

Single-module Android app (`app/`) targeting API 26+ with compileSdk 36.

- `app/src/main/java/com/heallog/` — Kotlin source files
- `app/src/main/java/com/heallog/ui/theme/` — Material3 theme: `Color.kt`, `Theme.kt`, `Type.kt`
- `app/src/main/res/` — Android resources (layouts, drawables, values)
- `app/src/test/` — JUnit unit tests
- `app/src/androidTest/` — Instrumented (on-device) tests

## Tech Stack

- **UI**: Jetpack Compose with Material3
- **Language**: Kotlin
- **Min SDK**: 26 (Android 8.0)
- **Build**: Gradle with Kotlin DSL (`build.gradle.kts`)
- Entry point: `MainActivity` uses `ComponentActivity` + `setContent {}` with edge-to-edge enabled

## App Overview

HealLog — 개인 사용자를 위한 부상 기록 안드로이드 앱.
바디맵에서 부위를 탭해 부상을 기록하고, 매일 통증 일지를 남기며, 회복 과정을 추적한다.

## Architecture

- MVVM pattern: ViewModel + StateFlow + UiState
- Room Database for local storage
- Hilt for dependency injection
- Coroutines + Flow for async
- Navigation Compose for routing
- Coil for image loading

## Package Structure (target)

- data/local/entity/ → Room entities
- data/local/dao/ → Room DAOs
- data/local/database/ → Room database class
- data/repository/ → Repository classes
- di/ → Hilt modules
- ui/home/ → Home dashboard
- ui/record/ → Injury recording screen
- ui/bodymap/ → Body map component
- ui/detail/ → Injury detail screen
- ui/theme/ → Theme, colors, typography
- model/ → Domain models and enums
- util/ → Utilities

## Conventions

- UI state uses sealed interface UiState pattern
- Korean UI strings, English code
- Include Compose @Preview for all screens
- Pain level range: 0-10
- InjuryStatus: ACTIVE, RECOVERING, HEALED