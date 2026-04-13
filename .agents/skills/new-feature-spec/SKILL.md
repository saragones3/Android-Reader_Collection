---
name: new-feature-spec
description: Specification for creating new features or refactoring existing ones, following the project's MVI/MVVM pattern in Jetpack Compose.
---

# Feature Specification: MVI with Jetpack Compose

This skill provides the necessary specifications, naming conventions, and templates to create new features in the **Reader Collection** project. All features must adhere to the pattern analyzed from existing implementations like `bookdetail` and `friends`.

## 1. Directory Structure & Naming
Each feature must be contained in its own package under `aragones.sergio.readercollection.presentation.<feature_name>`.

| Component | File Name | Purpose |
| :--- | :--- | :--- |
| **State** | `<Feature>UiState.kt` | Data/Sealed class representing the UI state. |
| **ViewModel** | `<Feature>ViewModel.kt` | Logic, state management, and repository interaction. |
| **View (Glue)** | `<Feature>View.kt` | Composable that collects state from ViewModel and handles Dialogs. |
| **Screen (UI)** | `<Feature>Screen.kt` | Stateless Composable for the main UI layout and Previews. |

## 2. Formatting & Styles

### File Headers
All Kotlin files **MUST** include the standard copyright header:
```kotlin
/*
 * Copyright (c) {{YEAR}} Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on {{DATE}}
 */
```

### ViewModel Regions
ViewModels **MUST** use `//region` to organize code:
1. `Private properties`
2. `Public properties`
3. `Lifecycle methods` (e.g., `onCreate`)
4. `Public methods` (Actions/Intents)
5. `Private methods`

## 3. Architecture Pattern (MVI-ish)

### UI State
- Defined as a `data class` for simple states or `sealed class` for Loading/Success/Error states.
- Must be immutable.

### ViewModel Actions
- Instead of a single `onIntent` function, use descriptive public functions (e.g., `fun deleteBook()`, `fun enableEdition()`).
- Use `viewModelScope.launch` for all asynchronous work.
- Use `MutableStateFlow.update { it.copy(...) }` to modify state.

### Side Effects (Dialogs & Errors)
- Common side effects like showing dialogs or errors should have their own `StateFlow`s (e.g., `_infoDialogMessageId`, `_error`).
- Use `org.jetbrains.compose.resources.StringResource` for dialog messages.

## 4. Compose Guidelines
- **Stateless Screens**: `Screen.kt` components should receive state and callbacks, making them easy to preview.
- **Previews**: Every Screen must have a `@CustomPreviewLightDark` with a `PreviewParameterProvider`.
- **Resources**: Use `Res.string.<name>` and `stringResource(...)`.
- **Glue Layer**: `View.kt` is the entry point, it uses `koinViewModel()` and wraps the screen in `ReaderCollectionApp`.

## 5. Theming & Design System

### Colors & Root Composable
All screens **MUST** be wrapped in `ReaderCollectionApp`.
- **Primary Color**: `MaterialTheme.colorScheme.primary` (EbonyClay in light, White in dark).
- **Secondary Color**: `MaterialTheme.colorScheme.secondary`.
- **Background**: `MaterialTheme.colorScheme.background`.

### Typography & Shapes
- Use `MaterialTheme.typography` (e.g., `displayLarge` for titles).
- Use `MaterialTheme.shapes` (e.g., `medium` for images/cards).

## 6. Common Components
Use the existing custom components in `presentation.components` to maintain consistency:
- `CustomToolbar`: For standard top app bars.
- `CustomOutlinedTextField`: For user input.
- `StarRatingBar`: For ratings.
- `ImageWithLoading`: For handling remote images with states.
- `ConfirmationAlertDialog` & `InformationAlertDialog`: For dialogs.

## 7. Dependency Injection
New ViewModels must be registered in `aragones.sergio.readercollection.presentation.di.PresentationModule.kt` using `viewModelOf(::<Feature>ViewModel)`.

## 8. ViewModel Testing
Every new feature **MUST** include ViewModel tests to ensure logic and state management work as expected.

### Guidelines
- **Location**: Place tests in `app/src/androidUnitTest/kotlin/aragones/sergio/readercollection/presentation/`.
- **Naming**: Use `<Feature>ViewModelTest.kt`.
- **Frameworks**:
    - **Mocking**: Use `io.mockk` for mocking dependencies.
    - **State Verification**: Use `Turbine` (via `.test { ... }`) to verify `StateFlow` emissions.
    - **Coroutines**: Use `runTest` from `kotlinx.coroutines.test`.
    - **Rule**: Use `MainDispatcherRule` to handle coroutine dispatchers in tests.

### Test Structure
Follow the **GIVEN-WHEN-THEN** pattern:
1. **GIVEN**: Mock dependencies and prepare initial data.
2. **WHEN**: Call the ViewModel method to be tested.
3. **THEN**: Assert the expected state changes and verify interactions with mocks.

---

## Templates

The following templates are available in the `templates/` directory of this skill:

- **[FeatureUiState.kt](file:///Users/sergioaragones/Desktop/Proyectos/Reader_Collection/.agents/skills/new-feature-spec/templates/FeatureUiState.kt)**: Template for the UI state data class.
- **[FeatureViewModel.kt](file:///Users/sergioaragones/Desktop/Proyectos/Reader_Collection/.agents/skills/new-feature-spec/templates/FeatureViewModel.kt)**: Template for the ViewModel with MVI-ish pattern.
- **[FeatureView.kt](file:///Users/sergioaragones/Desktop/Proyectos/Reader_Collection/.agents/skills/new-feature-spec/templates/FeatureView.kt)**: Template for the Compose glue layer.
- **[FeatureScreen.kt](file:///Users/sergioaragones/Desktop/Proyectos/Reader_Collection/.agents/skills/new-feature-spec/templates/FeatureScreen.kt)**: Template for the stateless Screen and previews.

