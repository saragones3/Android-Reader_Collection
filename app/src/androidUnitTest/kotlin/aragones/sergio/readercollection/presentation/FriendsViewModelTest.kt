/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 5/10/2025
 */

@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("ktlint:standard:max-line-length")

package aragones.sergio.readercollection.presentation

import app.cash.turbine.test
import aragones.sergio.readercollection.data.UserRepositoryImpl
import aragones.sergio.readercollection.data.local.UserLocalDataSource
import aragones.sergio.readercollection.data.remote.UserRemoteDataSource
import aragones.sergio.readercollection.data.remote.model.RequestStatus
import aragones.sergio.readercollection.data.remote.model.UserResponse
import aragones.sergio.readercollection.domain.BooksRepository
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.domain.model.User
import aragones.sergio.readercollection.domain.toRemoteData
import aragones.sergio.readercollection.presentation.friends.FriendsTab
import aragones.sergio.readercollection.presentation.friends.FriendsUiState
import aragones.sergio.readercollection.presentation.friends.FriendsViewModel
import aragones.sergio.readercollection.presentation.friends.UserUi
import aragones.sergio.readercollection.presentation.friends.UsersUi
import aragones.sergio.readercollection.presentation.utils.MainDispatcherRule
import com.aragones.sergio.util.BookState
import com.aragones.sergio.util.Constants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.friend_action_failure
import reader_collection.app.generated.resources.friend_action_successfully_done
import reader_collection.app.generated.resources.user_remove_confirmation

class FriendsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testUserId = "userId"
    private val testUsername = "username"
    private val userLocalDataSource: UserLocalDataSource = mockk {
        every { userId } returns testUserId
        every { username } returns testUsername
    }
    private val userRemoteDataSource: UserRemoteDataSource = mockk()
    private val booksRepository: BooksRepository = mockk()
    private val viewModel = FriendsViewModel(
        booksRepository = booksRepository,
        userRepository = UserRepositoryImpl(
            userLocalDataSource = userLocalDataSource,
            userRemoteDataSource = userRemoteDataSource,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        ),
    )

    @Test
    fun `GIVEN friends WHEN fetchFriends THEN returns Success state with friends list`() = runTest {
        val friend1 = User("1", "", RequestStatus.APPROVED)
        val friend2 = User("2", "", RequestStatus.PENDING_MINE)
        val friend3 = User("3", "", RequestStatus.PENDING_FRIEND)
        val friend4 = User("4", "", RequestStatus.REJECTED)
        coEvery {
            userRemoteDataSource.getFriends(any())
        } returns Result.success(
            listOf(
                friend1.toRemoteData(),
                friend2.toRemoteData(),
                friend3.toRemoteData(),
                friend4.toRemoteData(),
            ),
        )

        viewModel.state.test {
            assertEquals(FriendsUiState.Loading, awaitItem())

            viewModel.fetchFriends()

            assertEquals(
                FriendsUiState.Success(
                    friends = UsersUi(
                        listOf(
                            UserUi("1", "", false),
                            UserUi("2", "", true),
                        ),
                    ),
                    requests = UsersUi(
                        listOf(
                            UserUi("3", "", true),
                            UserUi("4", "", false),
                        ),
                    ),
                ),
                awaitItem(),
            )
        }
        coVerify { userRemoteDataSource.getFriends(testUserId) }
        confirmVerified(userRemoteDataSource)
    }

    @Test
    fun `GIVEN no friends WHEN fetchFriends THEN returns Success state with empty list`() =
        runTest {
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.success(emptyList())

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()

                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )
            }
            coVerify { userRemoteDataSource.getFriends(testUserId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN error on getting friends WHEN fetchFriends THEN returns Success state with empty list`() =
        runTest {
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.failure(RuntimeException("Firestore error"))

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()

                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )
            }
            coVerify { userRemoteDataSource.getFriends(testUserId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `WHEN selectTab THEN state is updated with the new tab`() = runTest {
        coEvery {
            userRemoteDataSource.getFriends(any())
        } returns Result.success(emptyList())

        viewModel.state.test {
            assertEquals(FriendsUiState.Loading, awaitItem())
            viewModel.fetchFriends()
            assertEquals(
                FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                awaitItem(),
            )

            viewModel.selectTab(FriendsTab.REQUESTS)

            assertEquals(
                FriendsUiState.Success(
                    tab = FriendsTab.REQUESTS,
                    friends = UsersUi(),
                    requests = UsersUi(),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `GIVEN query and success response WHEN searchFriends THEN search results are shown`() =
        runTest {
            val query = "query"
            val user = User("1", "", RequestStatus.PENDING_FRIEND)
            val users = listOf(user)
            val userUi = UserUi("1", "", true)
            coEvery {
                userRemoteDataSource.getFriends(testUserId)
            } returns Result.success(emptyList())
            coEvery {
                userRemoteDataSource.getUsers(query, testUserId)
            } returns Result.success(users.map { it.toRemoteData() })

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )

                viewModel.searchFriends(query)

                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = true,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(listOf(userUi)),
                    ),
                    awaitItem(),
                )
            }
            coVerify(exactly = 2) { userRemoteDataSource.getFriends(testUserId) }
            coVerify { userRemoteDataSource.getUsers(query, testUserId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN query and success response with non pending friend WHEN searchFriends THEN no search results are shown`() =
        runTest {
            val query = "query"
            val user = User("1", "", RequestStatus.APPROVED)
            val users = listOf(user)
            coEvery {
                userRemoteDataSource.getFriends(testUserId)
            } returns Result.success(emptyList())
            coEvery {
                userRemoteDataSource.getUsers(query, testUserId)
            } returns Result.success(users.map { it.toRemoteData() })

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )

                viewModel.searchFriends(query)

                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = true,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
            }
            coVerify(exactly = 2) { userRemoteDataSource.getFriends(testUserId) }
            coVerify { userRemoteDataSource.getUsers(query, testUserId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN query and failure response WHEN searchFriends THEN error is shown`() = runTest {
        val query = "query"
        coEvery { userRemoteDataSource.getFriends(testUserId) } returns Result.success(emptyList())
        coEvery {
            userRemoteDataSource.getUsers(query, testUserId)
        } returns Result.failure(RuntimeException())

        viewModel.error.test {
            val error = this
            assertEquals(null, awaitItem())

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )

                viewModel.searchFriends(query)

                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = true,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    ErrorModel(Constants.EMPTY_VALUE, Res.string.friend_action_failure),
                    error.awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
            }
        }
        coVerify { userRemoteDataSource.getFriends(testUserId) }
        coVerify { userRemoteDataSource.getUsers(query, testUserId) }
        confirmVerified(userRemoteDataSource)
    }

    @Test
    fun `GIVEN empty query WHEN searchFriends THEN state is updated but no search is performed`() =
        runTest {
            val query = "query"
            val user = User("1", "", RequestStatus.PENDING_FRIEND)
            val users = listOf(user)
            val userUi = UserUi("1", "", true)
            coEvery {
                userRemoteDataSource.getFriends(testUserId)
            } returns Result.success(emptyList())
            coEvery {
                userRemoteDataSource.getUsers(query, testUserId)
            } returns Result.success(users.map { it.toRemoteData() })

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )
                viewModel.searchFriends(query)
                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = true,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = query,
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(listOf(userUi)),
                    ),
                    awaitItem(),
                )

                viewModel.searchFriends("")

                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = "",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
            }
            coVerify(exactly = 2) { userRemoteDataSource.getFriends(testUserId) }
            coVerify { userRemoteDataSource.getUsers(query, testUserId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN friend and success response WHEN requestFriendship THEN friend is added to requests`() =
        runTest {
            val user = User("1", "user1", RequestStatus.PENDING_FRIEND)
            val users = listOf(user)
            val userUi = UserUi("1", "user1", true)
            coEvery {
                userRemoteDataSource.getFriends(testUserId)
            } returns Result.success(emptyList())
            coEvery {
                userRemoteDataSource.getUsers(any(), any())
            } returns Result.success(users.map { it.toRemoteData() })
            coEvery {
                userRemoteDataSource.requestFriendship(any(), any())
            } returns Result.success(Unit)

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )
                viewModel.searchFriends("user1")
                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = "user1",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = true,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = "user1",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(listOf(userUi)),
                    ),
                    awaitItem(),
                )

                viewModel.requestFriendship(userUi)

                assertEquals(
                    FriendsUiState.Success(
                        searchQuery = "user1",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(listOf(userUi.copy(isLoading = true))),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        tab = FriendsTab.REQUESTS,
                        searchQuery = "",
                        friends = UsersUi(),
                        requests = UsersUi(listOf(userUi)),
                        isSearching = false,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
            }
            coVerify(exactly = 2) { userRemoteDataSource.getFriends(testUserId) }
            coVerify { userRemoteDataSource.getUsers("user1", testUserId) }
            coVerify {
                userRemoteDataSource.requestFriendship(
                    UserResponse(
                        id = testUserId,
                        username = testUsername,
                        status = RequestStatus.PENDING_MINE,
                    ),
                    user.toRemoteData(),
                )
            }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN friend and failure response WHEN requestFriendship THEN error is shown`() = runTest {
        val user = User("1", "user1", RequestStatus.PENDING_FRIEND)
        val users = listOf(user)
        val userUi = UserUi("1", "user1", true)
        coEvery {
            userRemoteDataSource.getFriends(testUserId)
        } returns Result.success(emptyList())
        coEvery {
            userRemoteDataSource.getUsers(any(), any())
        } returns Result.success(users.map { it.toRemoteData() })
        coEvery {
            userRemoteDataSource.requestFriendship(any(), any())
        } returns Result.failure(RuntimeException())

        viewModel.error.test {
            val error = this
            assertEquals(null, awaitItem())

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())

                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                    awaitItem(),
                )
                viewModel.searchFriends("user1")
                assertEquals(
                    FriendsUiState.Success(
                        tab = FriendsTab.FRIENDS,
                        searchQuery = "user1",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = true,
                        searchResults = UsersUi(),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        tab = FriendsTab.FRIENDS,
                        searchQuery = "user1",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(listOf(userUi)),
                    ),
                    awaitItem(),
                )

                viewModel.requestFriendship(userUi)

                assertEquals(
                    FriendsUiState.Success(
                        tab = FriendsTab.FRIENDS,
                        searchQuery = "user1",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(listOf(userUi.copy(isLoading = true))),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    FriendsUiState.Success(
                        tab = FriendsTab.FRIENDS,
                        searchQuery = "user1",
                        friends = UsersUi(),
                        requests = UsersUi(),
                        isSearching = false,
                        searchResults = UsersUi(listOf(userUi)),
                    ),
                    awaitItem(),
                )
                assertEquals(
                    ErrorModel(Constants.EMPTY_VALUE, Res.string.friend_action_failure),
                    error.awaitItem(),
                )
            }
        }

        coVerify(exactly = 2) { userRemoteDataSource.getFriends(testUserId) }
        coVerify { userRemoteDataSource.getUsers("user1", testUserId) }
        coVerify {
            userRemoteDataSource.requestFriendship(
                UserResponse(
                    id = testUserId,
                    username = testUsername,
                    status = RequestStatus.PENDING_MINE,
                ),
                user.toRemoteData(),
            )
        }
        confirmVerified(userRemoteDataSource)
    }

    @Test
    fun `GIVEN friend id and success response WHEN acceptFriendRequest THEN success message is shown and friend status is updated to Approved`() =
        runTest {
            val friendId = "friendId"
            val friend = User(friendId, "", RequestStatus.PENDING_MINE)
            val friendUi = UserUi(friendId, "", true)
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.success(listOf(friend.toRemoteData()))
            coEvery {
                userRemoteDataSource.acceptFriendRequest(any(), any())
            } returns Result.success(Unit)

            viewModel.infoDialogMessageId.test {
                val infoDialogMessage = this
                assertEquals(null, awaitItem())

                viewModel.state.test {
                    val state = this
                    assertEquals(FriendsUiState.Loading, awaitItem())
                    viewModel.fetchFriends()
                    assertEquals(
                        FriendsUiState.Success(
                            friends = UsersUi(listOf(friendUi)),
                            requests = UsersUi(),
                        ),
                        awaitItem(),
                    )

                    viewModel.acceptFriendRequest(friendId)

                    assertEquals(
                        Res.string.friend_action_successfully_done,
                        infoDialogMessage.awaitItem(),
                    )
                    assertEquals(
                        FriendsUiState.Success(
                            friends = UsersUi(listOf(friendUi.copy(isPending = false))),
                            requests = UsersUi(),
                        ),
                        state.awaitItem(),
                    )
                }
            }
            coVerify { userRemoteDataSource.getFriends(testUserId) }
            coVerify { userRemoteDataSource.acceptFriendRequest(testUserId, friendId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN failure response WHEN acceptFriendRequest THEN error is shown`() = runTest {
        val friendId = "friendId"
        coEvery {
            userRemoteDataSource.acceptFriendRequest(any(), any())
        } returns Result.failure(RuntimeException("Firestore error"))

        viewModel.error.test {
            assertEquals(null, awaitItem())

            viewModel.acceptFriendRequest(friendId)

            assertEquals(
                ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.friend_action_failure,
                ),
                awaitItem(),
            )
        }

        coVerify { userRemoteDataSource.acceptFriendRequest(testUserId, friendId) }
        confirmVerified(userRemoteDataSource)
    }

    @Test
    fun `GIVEN friend id and success response WHEN rejectFriendRequest THEN success message is shown and friend is removed from list`() =
        runTest {
            val friendId = "friendId"
            val friend = User(friendId, "", RequestStatus.PENDING_MINE)
            val friendUi = UserUi(friendId, "", true)
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.success(listOf(friend.toRemoteData()))
            coEvery {
                userRemoteDataSource.rejectFriendRequest(any(), any())
            } returns Result.success(Unit)

            viewModel.infoDialogMessageId.test {
                val infoDialogMessage = this
                assertEquals(null, awaitItem())

                viewModel.state.test {
                    val state = this
                    assertEquals(FriendsUiState.Loading, awaitItem())
                    viewModel.fetchFriends()
                    assertEquals(
                        FriendsUiState.Success(
                            friends = UsersUi(listOf(friendUi)),
                            requests = UsersUi(),
                        ),
                        awaitItem(),
                    )

                    viewModel.rejectFriendRequest(friendId)

                    assertEquals(
                        Res.string.friend_action_successfully_done,
                        infoDialogMessage.awaitItem(),
                    )
                    assertEquals(
                        FriendsUiState.Success(friends = UsersUi(), requests = UsersUi()),
                        state.awaitItem(),
                    )
                }
            }
            coVerify { userRemoteDataSource.getFriends(testUserId) }
            coVerify { userRemoteDataSource.rejectFriendRequest(testUserId, friendId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN failure response WHEN rejectFriendRequest THEN error is shown`() = runTest {
        val friendId = "friendId"
        coEvery {
            userRemoteDataSource.rejectFriendRequest(any(), any())
        } returns Result.failure(RuntimeException("Firestore error"))

        viewModel.error.test {
            assertEquals(null, awaitItem())

            viewModel.rejectFriendRequest(friendId)

            assertEquals(
                ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.friend_action_failure,
                ),
                awaitItem(),
            )
        }

        coVerify { userRemoteDataSource.rejectFriendRequest(testUserId, friendId) }
        confirmVerified(userRemoteDataSource)
    }

    @Test
    fun `GIVEN success response and friend request rejected WHEN deleteFriend THEN success message is shown and friend is removed from list`() =
        runTest {
            val friendId = "friendId"
            val friend = User(friendId, "", RequestStatus.REJECTED)
            val friendUi = UserUi(friendId, "", false)
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.success(listOf(friend.toRemoteData()))
            coEvery {
                userRemoteDataSource.deleteFriend(any(), any())
            } returns Result.success(Unit)

            viewModel.infoDialogMessageId.test {
                val infoDialogMessage = this
                assertEquals(null, awaitItem())

                viewModel.state.test {
                    val state = this
                    assertEquals(FriendsUiState.Loading, awaitItem())
                    viewModel.fetchFriends()
                    assertEquals(
                        FriendsUiState.Success(
                            tab = FriendsTab.REQUESTS,
                            friends = UsersUi(),
                            requests = UsersUi(listOf(friendUi)),
                        ),
                        awaitItem(),
                    )

                    viewModel.deleteFriend(friendId)

                    assertEquals(
                        Res.string.friend_action_successfully_done,
                        infoDialogMessage.awaitItem(),
                    )
                    assertEquals(
                        FriendsUiState.Success(
                            tab = FriendsTab.FRIENDS,
                            friends = UsersUi(),
                            requests = UsersUi(),
                        ),
                        state.awaitItem(),
                    )
                }
            }
            coVerify { userRemoteDataSource.getFriends(testUserId) }
            coVerify { userRemoteDataSource.deleteFriend(testUserId, friendId) }
            confirmVerified(userRemoteDataSource)
        }

    @Test
    fun `GIVEN failure response WHEN deleteFriend THEN error is shown`() = runTest {
        val friendId = "friendId"
        coEvery {
            userRemoteDataSource.deleteFriend(any(), any())
        } returns Result.failure(RuntimeException("Firestore error"))

        viewModel.error.test {
            assertEquals(null, awaitItem())

            viewModel.deleteFriend(friendId)

            assertEquals(
                ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.friend_action_failure,
                ),
                awaitItem(),
            )
        }

        coVerify { userRemoteDataSource.deleteFriend(testUserId, friendId) }
        confirmVerified(userRemoteDataSource)
    }

    @Test
    fun `GIVEN no dialog shown WHEN showConfirmationDialog THEN dialog is shown`() = runTest {
        viewModel.userDeletionMessage.test {
            assertEquals(null, awaitItem())

            viewModel.showConfirmationDialog(Res.string.user_remove_confirmation, "userId")

            assertEquals(Res.string.user_remove_confirmation to "userId", awaitItem())
        }
    }

    @Test
    fun `GIVEN same dialog message shown WHEN showConfirmationDialog THEN do nothing`() = runTest {
        viewModel.userDeletionMessage.test {
            assertEquals(null, awaitItem())
            viewModel.showConfirmationDialog(Res.string.user_remove_confirmation, "userId")
            assertEquals(Res.string.user_remove_confirmation to "userId", awaitItem())

            viewModel.showConfirmationDialog(Res.string.user_remove_confirmation, "userId")

            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN dialog shown WHEN closeDialogs THEN dialog is reset`() = runTest {
        viewModel.userDeletionMessage.test {
            val userDeletionMessage = this
            assertEquals(null, awaitItem())

            viewModel.showConfirmationDialog(Res.string.user_remove_confirmation, "userId")
            assertEquals(Res.string.user_remove_confirmation to "userId", awaitItem())

            viewModel.infoDialogMessageId.test {
                val infoDialogMessage = this
                assertEquals(null, awaitItem())

                coEvery {
                    userRemoteDataSource.acceptFriendRequest(any(), any())
                } returns Result.success(Unit)
                viewModel.acceptFriendRequest("")
                assertEquals(
                    Res.string.friend_action_successfully_done,
                    awaitItem(),
                )

                viewModel.error.test {
                    val error = this
                    assertEquals(null, awaitItem())

                    coEvery {
                        userRemoteDataSource.rejectFriendRequest(any(), any())
                    } returns Result.failure(RuntimeException("Firestore error"))
                    viewModel.rejectFriendRequest("")
                    assertEquals(
                        ErrorModel(
                            Constants.EMPTY_VALUE,
                            Res.string.friend_action_failure,
                        ),
                        awaitItem(),
                    )

                    viewModel.closeDialogs()

                    assertEquals(null, infoDialogMessage.awaitItem())
                    assertEquals(null, userDeletionMessage.awaitItem())
                    assertEquals(null, error.awaitItem())
                }
            }
        }
    }

    @Test
    fun `GIVEN no dialog shown WHEN closeDialogs THEN do nothing`() = runTest {
        viewModel.userDeletionMessage.test {
            assertEquals(null, awaitItem())

            viewModel.infoDialogMessageId.test {
                assertEquals(null, awaitItem())

                viewModel.error.test {
                    assertEquals(null, awaitItem())

                    viewModel.closeDialogs()

                    expectNoEvents()
                }
            }
        }
    }

    @Test
    fun `GIVEN friend id and success response with books WHEN toggleLibrary THEN friend is expanded and books are shown`() =
        runTest {
            val friendId = "friendId"
            val friend = User(id = friendId, username = "", status = RequestStatus.APPROVED)
            val friendUi = UserUi(id = friendId, username = "", isPending = false)
            val book = Book("1").apply { this.state = BookState.READING }
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.success(listOf(friend.toRemoteData()))
            coEvery {
                booksRepository.getBooksFrom(friendId)
            } returns Result.success(listOf(book))

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())
                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(listOf(friendUi)),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )

                viewModel.toggleLibrary(friendId)

                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(
                            listOf(
                                friendUi.copy(
                                    isExpanded = true,
                                    hasBooks = true,
                                    readingBooks = listOf(book),
                                ),
                            ),
                        ),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )
            }
            coVerify { userRemoteDataSource.getFriends(testUserId) }
            coVerify { booksRepository.getBooksFrom(friendId) }
            confirmVerified(userRemoteDataSource, booksRepository)
        }

    @Test
    fun `GIVEN friend id and success response with no books WHEN toggleLibrary THEN friend is expanded and no books are shown`() =
        runTest {
            val friendId = "friendId"
            val friend = User(id = friendId, username = "", status = RequestStatus.APPROVED)
            val friendUi = UserUi(id = friendId, username = "", isPending = false)
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.success(listOf(friend.toRemoteData()))
            coEvery {
                booksRepository.getBooksFrom(friendId)
            } returns Result.success(emptyList())

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())
                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(listOf(friendUi)),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )

                viewModel.toggleLibrary(friendId)

                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(
                            listOf(
                                friendUi.copy(
                                    isExpanded = true,
                                    hasBooks = false,
                                ),
                            ),
                        ),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )
            }
            coVerify { userRemoteDataSource.getFriends(testUserId) }
            coVerify { booksRepository.getBooksFrom(friendId) }
            confirmVerified(userRemoteDataSource, booksRepository)
        }

    @Test
    fun `GIVEN friend id and failure response WHEN toggleLibrary THEN error is shown`() = runTest {
        val friendId = "friendId"
        val friend = User(id = friendId, username = "", status = RequestStatus.APPROVED)
        val friendUi = UserUi(id = friendId, username = "", isPending = false)
        coEvery {
            userRemoteDataSource.getFriends(any())
        } returns Result.success(listOf(friend.toRemoteData()))
        coEvery {
            booksRepository.getBooksFrom(friendId)
        } returns Result.failure(RuntimeException())

        viewModel.error.test {
            val error = this
            assertEquals(null, awaitItem())

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())
                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(listOf(friendUi)),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )

                viewModel.toggleLibrary(friendId)

                assertEquals(
                    ErrorModel(Constants.EMPTY_VALUE, Res.string.friend_action_failure),
                    error.awaitItem(),
                )
            }
        }
        coVerify { userRemoteDataSource.getFriends(testUserId) }
        coVerify { booksRepository.getBooksFrom(friendId) }
        confirmVerified(userRemoteDataSource, booksRepository)
    }

    @Test
    fun `GIVEN friend id and books already loaded WHEN toggleLibrary THEN friend expansion is toggled without fetching books`() =
        runTest {
            val friendId = "friendId"
            val friend = User(id = friendId, username = "", status = RequestStatus.APPROVED)
            val friendUi = UserUi(id = friendId, username = "", isPending = false)
            val book = Book("1").apply { this.state = BookState.READING }
            coEvery {
                userRemoteDataSource.getFriends(any())
            } returns Result.success(listOf(friend.toRemoteData()))
            coEvery {
                booksRepository.getBooksFrom(friendId)
            } returns Result.success(listOf(book))

            viewModel.state.test {
                assertEquals(FriendsUiState.Loading, awaitItem())
                viewModel.fetchFriends()
                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(listOf(friendUi)),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )
                viewModel.toggleLibrary(friendId)
                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(
                            listOf(
                                friendUi.copy(
                                    isExpanded = true,
                                    hasBooks = true,
                                    readingBooks = listOf(book),
                                ),
                            ),
                        ),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )

                viewModel.toggleLibrary(friendId)
                assertEquals(
                    FriendsUiState.Success(
                        friends = UsersUi(
                            listOf(
                                friendUi.copy(
                                    isExpanded = false,
                                    hasBooks = true,
                                    readingBooks = listOf(book),
                                ),
                            ),
                        ),
                        requests = UsersUi(),
                    ),
                    awaitItem(),
                )
            }
            coVerify(exactly = 1) { userRemoteDataSource.getFriends(testUserId) }
            coVerify(exactly = 1) { booksRepository.getBooksFrom(friendId) }
            confirmVerified(userRemoteDataSource, booksRepository)
        }
}
