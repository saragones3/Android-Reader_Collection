/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 15/7/2025
 */

package aragones.sergio.readercollection.presentation.addfriend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.data.remote.model.RequestStatus
import aragones.sergio.readercollection.domain.UserRepository
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.domain.model.User
import com.aragones.sergio.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.error_search
import reader_collection.app.generated.resources.error_server

class AddFriendsViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Properties
    val state: StateFlow<AddFriendsUiState>
        field = MutableStateFlow<AddFriendsUiState>(
            AddFriendsUiState.Success(
                users = UsersUi(),
                query = "",
            ),
        )
    val error: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    //endregion

    //region Public methods
    fun searchUserWith(username: String) = viewModelScope.launch {
        if (username.isNotEmpty()) {
            state.value = AddFriendsUiState.Loading(username)
            userRepository.getUsersWith(username).fold(
                onSuccess = { users ->
                    state.value = AddFriendsUiState.Success(
                        users = UsersUi(users.map { it.toUi() }),
                        query = username,
                    )
                },
                onFailure = {
                    when (it) {
                        is NoSuchElementException -> {
                            state.value = AddFriendsUiState.Success(
                                users = UsersUi(),
                                query = username,
                            )
                        }
                        else -> {
                            error.value = ErrorModel(
                                Constants.EMPTY_VALUE,
                                Res.string.error_server,
                            )
                            state.value = AddFriendsUiState.Success(
                                users = UsersUi(),
                                query = username,
                            )
                        }
                    }
                },
            )
        } else {
            state.value = AddFriendsUiState.Success(
                users = UsersUi(),
                query = username,
            )
        }
    }

    fun requestFriendship(friend: UserUi) = viewModelScope.launch {
        when (val currentState = state.value) {
            is AddFriendsUiState.Loading -> {}
            is AddFriendsUiState.Success -> {
                state.value = currentState.copy(
                    users = UsersUi(
                        currentState.users.users.map {
                            if (it.id == friend.id) {
                                friend.copy(isLoading = true)
                            } else {
                                it
                            }
                        },
                    ),
                )
            }
        }
        userRepository.requestFriendship(friend.toDomain()).fold(
            onSuccess = {
                when (val currentState = state.value) {
                    is AddFriendsUiState.Loading -> {}
                    is AddFriendsUiState.Success -> {
                        state.value = currentState.copy(
                            users = UsersUi(
                                currentState.users.users.map {
                                    if (it.id == friend.id) {
                                        friend.copy(
                                            status = RequestStatus.APPROVED,
                                            isLoading = false,
                                        )
                                    } else {
                                        it
                                    }
                                },
                            ),
                        )
                    }
                }
            },
            onFailure = {
                error.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_search,
                )
                state.value = AddFriendsUiState.Success(
                    users = UsersUi(),
                    query = "",
                )
            },
        )
    }

    fun closeDialogs() {
        error.value = null
    }
    //endregion
}

private fun User.toUi(): UserUi = UserUi(
    id = id,
    username = username,
    status = status,
    isLoading = false,
)

private fun UserUi.toDomain(): User = User(
    id = id,
    username = username,
    status = status,
)