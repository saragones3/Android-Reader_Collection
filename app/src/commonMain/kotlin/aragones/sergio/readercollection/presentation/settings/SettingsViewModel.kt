/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 18/10/2020
 */

package aragones.sergio.readercollection.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.domain.BooksRepository
import aragones.sergio.readercollection.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class SettingsViewModel(
    private val booksRepository: BooksRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    val state: StateFlow<SettingsUiState>
        field = MutableStateFlow<SettingsUiState>(SettingsUiState("", false))
    val logOut: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)
    val confirmationDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    //endregion

    //region Lifecycle methods
    fun onResume() {
        state.update {
            it.copy(version = userRepository.getAppVersion())
        }
    }
    //endregion

    //region Public methods
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
        confirmationDialogMessageId.value = null
    }
    //endregion
}