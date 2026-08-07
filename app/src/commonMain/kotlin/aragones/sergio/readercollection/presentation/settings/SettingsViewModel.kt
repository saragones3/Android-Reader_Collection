/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 18/10/2020
 */

package aragones.sergio.readercollection.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.domain.BooksRepository
import aragones.sergio.readercollection.domain.UserRepository
import aragones.sergio.readercollection.domain.model.ErrorModel
import com.aragones.sergio.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.data_sync_successfully
import reader_collection.app.generated.resources.error_server

class SettingsViewModel(
    private val booksRepository: BooksRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    val state: StateFlow<SettingsUiState>
        field = MutableStateFlow<SettingsUiState>(SettingsUiState.empty())
    val relaunch: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)
    val logOut: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val confirmationDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val error: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    //endregion

    //region Lifecycle methods
    fun onResume() {
        state.update {
            it.copy(
                isProfilePublic = userRepository.isProfilePublic,
                isAutomaticSyncEnabled = userRepository.isAutomaticSyncEnabled,
                lastSynced = "", // TODO:
                language = userRepository.language,
                sortParam = userRepository.sortParam,
                isSortDescending = userRepository.isSortDescending,
                themeMode = userRepository.themeMode,
                version = userRepository.getAppVersion(),
            )
        }
    }
    //endregion

    //region Public methods
    fun setPublicProfile(value: Boolean) = viewModelScope.launch {
        state.update { it.copy(isLoading = true) }
        userRepository.setPublicProfile(value).fold(
            onSuccess = {
                state.update {
                    it.copy(
                        isProfilePublic = value,
                        isLoading = false,
                    )
                }
            },
            onFailure = {
                state.value = state.value.copy(isLoading = false)
                error.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_server,
                )
            },
        )
    }

    fun changeAutomaticSync(value: Boolean) {
        userRepository.storeAutomaticSync(value)
        state.value = state.value.copy(isAutomaticSyncEnabled = value)
    }

    fun syncData() = viewModelScope.launch {
        state.value = state.value.copy(isLoading = true)
        booksRepository.syncBooks(userRepository.userId).fold(
            onSuccess = {
                infoDialogMessageId.value = Res.string.data_sync_successfully
                state.value = state.value.copy(isLoading = false)
            },
            onFailure = {
                state.value = state.value.copy(isLoading = false)
                error.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_server,
                )
            },
        )
    }

    fun displaySettingsChanged(
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
        save()
    }

    fun logout() = viewModelScope.launch {
        state.update { it.copy(isLoading = true) }
        userRepository.logout()
        booksRepository.resetTable().fold(
            onSuccess = {
                state.update { it.copy(isLoading = false) }
                logOut.value = true
            },
            onFailure = {
                state.update { it.copy(isLoading = false) }
                logOut.value = true
            },
        )
    }

    fun showConfirmationDialog(textId: StringResource) {
        confirmationDialogMessageId.value = textId
    }

    fun closeDialogs() {
        infoDialogMessageId.value = null
        confirmationDialogMessageId.value = null
        error.value = null
    }
    //endregion

    private fun save() {
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
}