# Feature Implementation Workflow - Reader Collection

This workflow defines the standard process for implementing new features in the **Reader Collection** project, ensuring consistency with the Kotlin Multiplatform (KMP) architecture and the established MVI/MVVM pattern.

---

## 1. Input & Requirement Analysis
Before writing any code, ensure you have all the necessary information.

### Inputs
- **Screenshots/Mocks**: Visual reference for the UI.
- **Task Description**: Clear definition of the objective and acceptance criteria.
- **API Definition**: Endpoints, request/response models (if remote).
- **Persistence Needs**: Data to be stored locally in Room (Mobile) or SharedPreferences/DataStore.

### Checklist
- [ ] Understand the user's goal and the feature's scope.
- [ ] Identify if new icons or strings are needed (check `app/src/commonMain/composeResources`).
- [ ] Plan the navigation: Where does this feature live? (Check `presentation.navigation`).
- [ ] Consider platform differences (Android, iOS, Web).

---

## 2. Domain Layer
Define the business logic and data models.

### Checklist
- [ ] Create domain models in `aragones.sergio.readercollection.domain.model`.
- [ ] Define or update the Repository interface in `aragones.sergio.readercollection.domain`.
- [ ] Implement necessary Mappers in `aragones.sergio.readercollection.domain.Mappers.kt` to convert between Data/Local and Domain models.

---

## 3. Data Layer
Implement data fetching and persistence logic.

### Checklist
- [ ] **Remote**: If using Ktor, define the service and DTOs in `data.remote`. If using Firebase, update the corresponding service.
- [ ] **Local**: If persistence is needed:
    - [ ] Define the Entity and DAO in `core:database`.
    - [ ] Update `ReaderCollectionDatabase` to include the new DAO.
- [ ] **Repository**: Implement the Repository interface in `aragones.sergio.readercollection.data`.
- [ ] **DI**: Register the Repository implementation and any new data sources in `data.di.DataModule.kt`.

---

## 4. Presentation Layer (MVI-ish Pattern)
Follow the standards defined in the `new-feature-spec` skill.

### Checklist
- [ ] Create the feature package: `presentation.<feature_name>`.
- [ ] **UiState**: Create `<Feature>UiState.kt` (Immutable data/sealed class).
- [ ] **ViewModel**: Create `<Feature>ViewModel.kt` following the region structure (Properties, Lifecycle, Public/Private methods).
- [ ] **Screen**: Create `<Feature>Screen.kt` (Stateless Composable + Previews).
- [ ] **View**: Create `<Feature>View.kt` (Glue layer: collects state from ViewModel).
- [ ] **Components**: Reuse existing components from `presentation.components` (Toolbar, TextFields, etc.).

---

## 5. Integration & Navigation
Connect the feature to the rest of the app.

### Checklist
- [ ] **DI**: Register the ViewModel in `presentation.di.PresentationModule.kt` using `viewModelOf(::<Feature>ViewModel)`.
- [ ] **Navigation**: Add the new screen to `presentation.navigation.Navigation.kt` or the relevant navigation graph.
- [ ] **Strings**: Add all new strings to `strings.xml` in `composeResources`.

---

## 6. Quality & Testing
Ensure the feature is robust and follows project styles.

### Checklist
- [ ] **Testing**: Create ViewModel tests in `app/src/androidUnitTest/.../presentation/` using Mockk and Turbine.
- [ ] **Copyright**: Ensure all new files have the standard copyright header.
- [ ] **Static Analysis**: Run `./gradlew detekt` and `./gradlew spotlessApply` to ensure code style compliance.
- [ ] **Cleanup**: Remove any unused imports or placeholder code.
