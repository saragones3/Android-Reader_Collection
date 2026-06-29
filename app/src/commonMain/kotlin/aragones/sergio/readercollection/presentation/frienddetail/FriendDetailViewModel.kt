/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 16/7/2025
 */

package aragones.sergio.readercollection.presentation.frienddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import aragones.sergio.readercollection.domain.BooksRepository
import aragones.sergio.readercollection.domain.UserRepository
import aragones.sergio.readercollection.domain.model.Books
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.presentation.navigation.Route
import com.aragones.sergio.util.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.error_search
import reader_collection.app.generated.resources.error_server
import reader_collection.app.generated.resources.friend_removed
import reader_collection.app.generated.resources.no_friends_found

class FriendDetailViewModel(
    state: SavedStateHandle,
    private val booksRepository: BooksRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val params = state.toRoute<Route.FriendDetail>()
    val state: StateFlow<FriendDetailUiState>
        field = MutableStateFlow<FriendDetailUiState>(FriendDetailUiState.Loading)
    val confirmationDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val error: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    //endregion

    //region Public methods
    fun fetchFriend() = viewModelScope.launch {
        val friendRequest = async { userRepository.getFriend(params.userId) }
        val booksRequest = async { booksRepository.getBooksFrom(params.userId) }

        val friendResult = friendRequest.await()
        val booksResult = booksRequest.await()

        val friend = friendResult.getOrNull()
        val books = booksResult.getOrNull()

        if (friend != null && books != null) {
            state.value = FriendDetailUiState.Success(
                friend = friend,
                books = Books(books),
            )
        } else {
            val errorResult = friendResult.exceptionOrNull() ?: booksResult.exceptionOrNull()
            when (errorResult) {
                is NoSuchElementException -> {
                    error.value = ErrorModel(
                        Constants.EMPTY_VALUE,
                        Res.string.no_friends_found,
                    )
                }
                else -> {
                    error.value = ErrorModel(
                        Constants.EMPTY_VALUE,
                        Res.string.error_server,
                    )
                }
            }
        }
    }

    fun deleteFriend() = viewModelScope.launch {
        val currentState = state.value
        state.value = FriendDetailUiState.Loading
        userRepository.deleteFriend(params.userId).fold(
            onSuccess = {
                infoDialogMessageId.value = Res.string.friend_removed
            },
            onFailure = {
                error.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_search,
                )
                state.value = currentState
            },
        )
    }

    fun showConfirmationDialog(textId: StringResource) {
        confirmationDialogMessageId.value = textId
    }

    fun closeDialogs() {
        confirmationDialogMessageId.value = null
        infoDialogMessageId.value = null
        error.value = null
    }
    //endregion
}