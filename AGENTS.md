# Reader Collection - AI Agents Guide

Welcome to the Reader Collection project. This document provides essential information for AI agents to understand and contribute effectively to this codebase.

## Project Overview
Reader Collection is a Kotlin Multiplatform (KMP) application designed to track books (read, currently reading, or to be read). It features social elements like friend libraries and detailed book statistics.

## Technical Stack
- **Language**: Kotlin
- **Framework**: Kotlin Multiplatform (KMP)
- **UI**: Compose Multiplatform (Android, iOS & Web)
- **Architecture**: MVVM with Flows and Coroutines
- **Dependency Injection**: Koin
- **Networking**: Ktor
- **Database**: Room (via `core:database`)
- **Serialization**: Kotlinx Serialization
- **Cloud Services**: Firebase (Auth, Firestore, Crashlytics, Remote Config)

## Project Structure
- `app`: Main application module containing UI, domain, and data logic.
    - `presentation`: UI components and ViewModels (MVI-ish pattern).
    - `domain`: Business logic, models, and repository interfaces.
    - `data`: Repository implementations, remote (Ktor/Firebase) and local (SharedPreferences) data sources.
- `core`: Shared logic and infrastructure.
    - `database`: Room database definitions and DAOs (Android & iOS only).
    - `util`: Common utility functions and constants.

## Guidelines for Agents
1. **Consistency**: Follow the existing MVVM pattern.
2. **KMP Compatibility**: When working in shared modules, ensure code is compatible with Android, iOS & Web.
3. **Resources**: Use Compose Multiplatform resources for strings and images.
4. **Style**: Adhere to the project's Ktlint and Detekt configurations.
