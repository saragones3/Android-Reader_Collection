/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 1/7/2025
 */

package aragones.sergio.readercollection.presentation.displaysettings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import aragones.sergio.readercollection.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DisplaySettingsViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    val state: State<DisplaySettingsUiState>
        field = mutableStateOf<DisplaySettingsUiState>(
            DisplaySettingsUiState.empty().copy(
                language = userRepository.language,
                sortParam = userRepository.sortParam,
                isSortDescending = userRepository.isSortDescending,
                themeMode = userRepository.themeMode,
            ),
        )
    val relaunch: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)
    //endregion

    //region Lifecycle methods
    fun onResume() {
        state.value = state.value.copy(
            language = userRepository.language,
            sortParam = userRepository.sortParam,
            isSortDescending = userRepository.isSortDescending,
            themeMode = userRepository.themeMode,
        )
    }
    //endregion

    //region Public methods
    fun save() {
        val newLanguage = requireNotNull(state.value.language)
        val newSortParam = state.value.sortParam
        val newIsSortDescending = requireNotNull(state.value.isSortDescending)
        val newThemeMode = requireNotNull(state.value.themeMode)

        val changeLanguage = newLanguage != userRepository.language
        val changeSortParam = newSortParam != userRepository.sortParam
        val changeIsSortDescending = newIsSortDescending != userRepository.isSortDescending
        val changeThemeMode = newThemeMode != userRepository.themeMode

        if (changeLanguage) {
            userRepository.storeLanguage(newLanguage)
        }

        if (changeSortParam) {
            userRepository.storeSortParam(newSortParam)
        }

        if (changeIsSortDescending) {
            userRepository.storeIsSortDescending(newIsSortDescending)
        }

        if (changeThemeMode) {
            userRepository.storeThemeMode(newThemeMode)
            userRepository.applyTheme()
        }

        if (changeSortParam || changeIsSortDescending || changeThemeMode) {
            relaunch.value = true
        }
    }

    fun profileDataChanged(
        newLanguage: String,
        newSortParam: String?,
        newIsSortDescending: Boolean,
        newThemeMode: Int,
    ) {
        state.value = state.value.copy(
            language = newLanguage,
            sortParam = newSortParam,
            isSortDescending = newIsSortDescending,
            themeMode = newThemeMode,
        )
    }
    //endregion
}