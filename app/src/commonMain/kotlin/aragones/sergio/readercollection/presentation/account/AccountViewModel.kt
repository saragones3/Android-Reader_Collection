/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 30/6/2025
 */

package aragones.sergio.readercollection.presentation.account

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
import reader_collection.app.generated.resources.error_server
import reader_collection.app.generated.resources.invalid_email
import reader_collection.app.generated.resources.invalid_password
import reader_collection.app.generated.resources.verify_email_message

class AccountViewModel(
    private val booksRepository: BooksRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Properties
    val state: StateFlow<AccountUiState>
        field = MutableStateFlow<AccountUiState>(
            AccountUiState.empty().copy(
                username = userRepository.username,
                email = userRepository.userData.email,
                password = userRepository.userData.password,
            ),
        )
    val profileError: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    val logOut: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)
    val confirmationDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    //endregion

    //region Lifecycle methods
    fun onResume() {
        state.update {
            it.copy(
                email = userRepository.userData.email,
                emailError = null,
                password = userRepository.userData.password,
                passwordError = null,
                isProfilePublic = userRepository.isProfilePublic,
            )
        }
    }
    //endregion

    //region Public methods
    fun save() {
        val newEmail = state.value.email
        val newPassword = state.value.password

        if (newEmail != userRepository.userData.email ||
            newPassword != userRepository.userData.password
        ) {
            state.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                var result = Result.success(Unit)
                if (newEmail != userRepository.userData.email) {
                    result = userRepository.updateEmail(newEmail)
                    if (result.isSuccess) {
                        userRepository.updateDisplayName(userRepository.username)
                        showInfoDialog(Res.string.verify_email_message)
                    }
                }
                if (result.isSuccess && newPassword != userRepository.userData.password) {
                    result = userRepository.updatePassword(newPassword)
                }

                result.fold(
                    onSuccess = {
                        state.update { it.copy(isLoading = false) }
                    },
                    onFailure = {
                        manageError(ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server))
                    },
                )
            }
        }
    }

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
                manageError(ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server))
            },
        )
    }

    fun deleteUser() = viewModelScope.launch {
        state.update { it.copy(isLoading = true) }
        userRepository.deleteUser().fold(
            onSuccess = {
                resetDatabase()
            },
            onFailure = {
                manageError(ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server))
            },
        )
    }

    fun profileDataChanged(newEmail: String, newPassword: String) {
        var emailError: StringResource? = null
        if (newEmail.isNotBlank() && !Constants.isEmailValid(newEmail)) {
            emailError = Res.string.invalid_email
        }
        var passwordError: StringResource? = null
        if (!Constants.isPasswordValid(newPassword)) {
            passwordError = Res.string.invalid_password
        }
        state.update {
            it.copy(
                email = newEmail,
                emailError = emailError,
                password = newPassword,
                passwordError = passwordError,
            )
        }
    }

    fun showConfirmationDialog(textId: StringResource) {
        confirmationDialogMessageId.value = textId
    }

    fun showInfoDialog(textId: StringResource) {
        infoDialogMessageId.value = textId
    }

    fun closeDialogs() {
        confirmationDialogMessageId.value = null
        infoDialogMessageId.value = null
        profileError.value = null
    }
    //endregion

    //region Private methods
    private suspend fun resetDatabase() {
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

    private fun manageError(error: ErrorModel) {
        state.update { it.copy(isLoading = false) }
        profileError.value = error
    }
    //endregion
}