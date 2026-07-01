/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 1/7/2025
 */

package aragones.sergio.readercollection.presentation.datasync

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.domain.BooksRepository
import aragones.sergio.readercollection.domain.UserRepository
import aragones.sergio.readercollection.domain.model.ErrorModel
import com.aragones.sergio.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.data_sync_successfully
import reader_collection.app.generated.resources.error_server

class DataSyncViewModel(
    private val booksRepository: BooksRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val userId: String
        get() = userRepository.userId
    val state: State<DataSyncUiState>
        field = mutableStateOf<DataSyncUiState>(
            DataSyncUiState.empty().copy(
                isAutomaticSyncEnabled = userRepository.isAutomaticSyncEnabled,
            ),
        )
    val error: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val confirmationDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    //endregion

    //region Public methods
    fun changeAutomaticSync(value: Boolean) {
        userRepository.storeAutomaticSync(value)
        state.value = state.value.copy(isAutomaticSyncEnabled = value)
    }
    fun syncData() = viewModelScope.launch {
        state.value = state.value.copy(isLoading = true)
        booksRepository.syncBooks(userId).fold(
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

    fun showConfirmationDialog(textId: StringResource) {
        confirmationDialogMessageId.value = textId
    }

    fun closeDialogs() {
        infoDialogMessageId.value = null
        confirmationDialogMessageId.value = null
        error.value = null
    }
    //endregion
}