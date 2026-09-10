/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 28/10/2020
 */

package aragones.sergio.readercollection.presentation.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.data.remote.model.CustomExceptions
import aragones.sergio.readercollection.domain.UserRepository
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.presentation.login.model.LoginFormState
import com.aragones.sergio.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.error_server
import reader_collection.app.generated.resources.error_user_found
import reader_collection.app.generated.resources.invalid_email
import reader_collection.app.generated.resources.invalid_password
import reader_collection.app.generated.resources.invalid_repeat_password
import reader_collection.app.generated.resources.invalid_username

class RegisterViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    val state: State<RegisterUiState>
        field = mutableStateOf<RegisterUiState>(RegisterUiState.empty())
    val registerError: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val registerSuccess: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)
    //endregion

    //region Public methods
    fun register(username: String, password: String) = viewModelScope.launch {
        state.value = state.value.copy(isLoading = true)
        userRepository.register(username, password).fold(
            onSuccess = {
                userRepository.login(username, password).fold(
                    onSuccess = {
                        userRepository.setPublicProfile(true)
                        state.value = state.value.copy(isLoading = false)
                        registerSuccess.value = true
                    },
                    onFailure = {
                        manageError(
                            ErrorModel(
                                Constants.EMPTY_VALUE,
                                Res.string.error_server,
                            ),
                        )
                    },
                )
            },
            onFailure = {
                manageError(
                    if (it is CustomExceptions.ExistentUser) {
                        ErrorModel(
                            Constants.EMPTY_VALUE,
                            Res.string.error_user_found,
                        )
                    } else {
                        ErrorModel(
                            Constants.EMPTY_VALUE,
                            Res.string.error_server,
                        )
                    },
                )
            },
        )
    }

    fun registerDataChanged(username: String, password: String, confirmPassword: String) {
        var usernameError: StringResource? = null
        var passwordError: StringResource? = null
        var isDataValid = true

        if (username.contains("@")) {
            if (!Constants.isEmailValid(username)) {
                usernameError = Res.string.invalid_email
                isDataValid = false
            }
        } else if (!Constants.isUserNameValid(username)) {
            usernameError = Res.string.invalid_username
            isDataValid = false
        }
        if (!Constants.isPasswordValid(password)) {
            passwordError = Res.string.invalid_password
            isDataValid = false
        }
        if (password != confirmPassword) {
            passwordError = Res.string.invalid_repeat_password
            isDataValid = false
        }

        state.value = state.value.copy(
            username = username,
            password = password,
            confirmPassword = confirmPassword,
            formState = LoginFormState(usernameError, passwordError, isDataValid),
        )
    }

    fun showInfoDialog(textId: StringResource) {
        infoDialogMessageId.value = textId
    }

    fun closeDialogs() {
        registerError.value = null
        infoDialogMessageId.value = null
    }
    //endregion

    //region Private methods
    private fun manageError(error: ErrorModel) {
        state.value = state.value.copy(isLoading = false)
        registerError.value = error
    }
    //endregion
}