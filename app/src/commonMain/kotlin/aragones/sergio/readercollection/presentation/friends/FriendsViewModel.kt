/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/7/2025
 */

package aragones.sergio.readercollection.presentation.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.data.remote.model.RequestStatus
import aragones.sergio.readercollection.domain.UserRepository
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.domain.model.User
import com.aragones.sergio.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.friend_action_failure
import reader_collection.app.generated.resources.friend_action_successfully_done

class FriendsViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Properties
    val state: StateFlow<FriendsUiState>
        field = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val error: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    //endregion

    //region Public methods
    fun fetchFriends() = viewModelScope.launch {
        state.value = FriendsUiState.Loading
        val (friends, requests) = userRepository.getFriends().run {
            filter {
                it.status == RequestStatus.APPROVED ||
                    it.status == RequestStatus.PENDING_MINE
            }.map { it.toFriend() } to
                filter {
                    it.status == RequestStatus.REJECTED ||
                        it.status == RequestStatus.PENDING_FRIEND
                }.map { it.toRequest() }
        }
        state.value = FriendsUiState.Success(
            tab = getTab(friends, requests),
            friends = UsersUi(friends),
            requests = UsersUi(requests),
        )
    }

    fun selectTab(tab: FriendsTab) {
        state.update {
            when (it) {
                is FriendsUiState.Loading -> it
                is FriendsUiState.Success -> it.copy(tab = tab)
            }
        }
    }

    fun acceptFriendRequest(friendId: String) = viewModelScope.launch {
        userRepository.acceptFriendRequest(friendId).fold(
            onSuccess = {
                infoDialogMessageId.value = Res.string.friend_action_successfully_done
                state.update {
                    when (it) {
                        is FriendsUiState.Loading -> {
                            it
                        }
                        is FriendsUiState.Success -> {
                            val newFriends = UsersUi(
                                it.friends.users.map { friend ->
                                    if (friend.id == friendId) {
                                        friend.copy(isPending = false)
                                    } else {
                                        friend
                                    }
                                },
                            )
                            it.copy(
                                tab = getTab(newFriends.users, it.requests.users),
                                friends = newFriends,
                            )
                        }
                    }
                }
            },
            onFailure = {
                error.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.friend_action_failure,
                )
            },
        )
    }

    fun rejectFriendRequest(friendId: String) = viewModelScope.launch {
        userRepository.rejectFriendRequest(friendId).fold(
            onSuccess = {
                infoDialogMessageId.value = Res.string.friend_action_successfully_done
                state.update {
                    when (it) {
                        is FriendsUiState.Loading -> {
                            it
                        }
                        is FriendsUiState.Success -> {
                            val newFriends = UsersUi(
                                it.friends.users.filter { friend -> friend.id != friendId },
                            )
                            it.copy(
                                tab = getTab(newFriends.users, it.requests.users),
                                friends = newFriends,
                            )
                        }
                    }
                }
            },
            onFailure = {
                error.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.friend_action_failure,
                )
            },
        )
    }

    fun deleteFriend(friendId: String) = viewModelScope.launch {
        userRepository.deleteFriend(friendId).fold(
            onSuccess = {
                infoDialogMessageId.value = Res.string.friend_action_successfully_done
                state.update {
                    when (it) {
                        is FriendsUiState.Loading -> {
                            it
                        }
                        is FriendsUiState.Success -> {
                            val newRequests = UsersUi(
                                it.requests.users.filter { friend -> friend.id != friendId },
                            )
                            it.copy(
                                tab = getTab(it.friends.users, newRequests.users),
                                requests = newRequests,
                            )
                        }
                    }
                }
            },
            onFailure = {
                error.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.friend_action_failure,
                )
            },
        )
    }

    fun closeDialogs() {
        infoDialogMessageId.value = null
        error.value = null
    }
    //endregion

    private fun getTab(friends: List<UserUi>, requests: List<UserUi>): FriendsTab =
        if (friends.isEmpty() && requests.isNotEmpty()) {
            FriendsTab.REQUESTS
        } else {
            FriendsTab.FRIENDS
        }

    private fun User.toFriend(): UserUi = UserUi(
        id = id,
        username = username,
        isPending = status == RequestStatus.PENDING_MINE,
    )

    private fun User.toRequest(): UserUi = UserUi(
        id = id,
        username = username,
        isPending = status == RequestStatus.PENDING_FRIEND,
    )
}