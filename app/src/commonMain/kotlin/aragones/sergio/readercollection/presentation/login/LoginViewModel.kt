/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/10/2020
 */

package aragones.sergio.readercollection.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.domain.BooksRepository
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
import reader_collection.app.generated.resources.invalid_email
import reader_collection.app.generated.resources.invalid_password
import reader_collection.app.generated.resources.invalid_username
import reader_collection.app.generated.resources.wrong_credentials

class LoginViewModel(
    private val booksRepository: BooksRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    val state: State<LoginUiState>
        field = mutableStateOf<LoginUiState>(
            LoginUiState.empty().copy(username = userRepository.username),
        )
    val loginError: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    val loginSuccess: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)
    //endregion

    //region Public methods
    fun login(username: String, password: String) = viewModelScope.launch {
        state.value = state.value.copy(isLoading = true)
        userRepository.login(username, password).fold(
            onSuccess = {
                userRepository.loadConfig()
                booksRepository.loadBooks(userRepository.userId).fold(
                    onSuccess = {
                        state.value = state.value.copy(isLoading = false)
                        loginSuccess.value = true
                    },
                    onFailure = {
                        userRepository.logout()
                        state.value = state.value.copy(isLoading = false)
                        loginError.value = ErrorModel(
                            Constants.EMPTY_VALUE,
                            Res.string.error_server,
                        )
                    },
                )
            },
            onFailure = {
                state.value = state.value.copy(isLoading = false)
                loginError.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.wrong_credentials,
                )
            },
        )
    }

    fun loginDataChanged(username: String, password: String) {
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

        state.value = state.value.copy(
            username = username,
            password = password,
            formState = LoginFormState(usernameError, passwordError, isDataValid),
        )
    }

    fun closeDialogs() {
        loginError.value = null
    }
    //endregion
}