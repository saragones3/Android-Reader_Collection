/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 8/10/2025
 */

@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("ktlint:standard:max-line-length")

package aragones.sergio.readercollection.presentation

import app.cash.turbine.test
import aragones.sergio.readercollection.data.BooksRepositoryImpl
import aragones.sergio.readercollection.data.UserRepositoryImpl
import aragones.sergio.readercollection.data.local.BooksLocalDataSource
import aragones.sergio.readercollection.data.local.UserLocalDataSource
import aragones.sergio.readercollection.data.local.model.AuthData
import aragones.sergio.readercollection.data.local.model.UserData
import aragones.sergio.readercollection.data.remote.BooksRemoteDataSource
import aragones.sergio.readercollection.data.remote.UserRemoteDataSource
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.presentation.account.AccountUiState
import aragones.sergio.readercollection.presentation.account.AccountViewModel
import aragones.sergio.readercollection.presentation.utils.MainDispatcherRule
import com.aragones.sergio.util.Constants
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.error_server
import reader_collection.app.generated.resources.invalid_email
import reader_collection.app.generated.resources.invalid_password
import reader_collection.app.generated.resources.profile_delete_confirmation
import reader_collection.app.generated.resources.username_info

class AccountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testUserId = "userId"
    private val testUsername = "username"
    private val testPassword = "password"
    private val testEmail = ""
    private val booksLocalDataSource: BooksLocalDataSource = mockk {
        every { retrieveRemoteConfigValues() } just Runs
    }
    private val booksRemoteDataSource: BooksRemoteDataSource = mockk()
    private val userLocalDataSource: UserLocalDataSource = mockk {
        every { userId } returns testUserId
        every { username } returns testUsername
        every { userData } returns UserData(testUsername, testEmail, testPassword)
    }
    private val userRemoteDataSource: UserRemoteDataSource = mockk()
    private val viewModel = AccountViewModel(
        BooksRepositoryImpl(
            booksLocalDataSource,
            booksRemoteDataSource,
            mainDispatcherRule.testDispatcher,
        ),
        UserRepositoryImpl(
            userLocalDataSource,
            userRemoteDataSource,
            mainDispatcherRule.testDispatcher,
        ),
    )
    private val initialState = AccountUiState.empty().copy(
        username = testUsername,
        email = testEmail,
        password = testPassword,
    )

    @Test
    fun `WHEN onResume THEN updates state with repository data`() = runTest {
        val userData = UserData("username", "email", "password")
        val isPublicProfile = true
        every { userLocalDataSource.userData } returns userData
        every { userLocalDataSource.isProfilePublic } returns isPublicProfile

        viewModel.state.test {
            assertEquals(initialState, awaitItem())

            viewModel.onResume()

            assertEquals(
                initialState.copy(
                    email = userData.email,
                    emailError = null,
                    password = userData.password,
                    passwordError = null,
                    isProfilePublic = isPublicProfile,
                ),
                awaitItem(),
            )
        }
        verify { userLocalDataSource.userData }
        verify { userLocalDataSource.isProfilePublic }
    }

    @Test
    fun `GIVEN new password and success response WHEN save THEN updates password`() = runTest {
        val newPassword = "123456"
        viewModel.profileDataChanged(testEmail, newPassword)
        coEvery { userRemoteDataSource.login(any(), any()) } returns Result.success(testUserId)
        coEvery { userRemoteDataSource.updatePassword(any()) } returns Result.success(Unit)
        every { userLocalDataSource.storePassword(any()) } just Runs
        every { userLocalDataSource.storeCredentials(any()) } just Runs

        viewModel.state.test {
            val updatedInitialState = initialState.copy(
                password = newPassword,
            )
            assertEquals(updatedInitialState, awaitItem())

            viewModel.save()

            assertEquals(
                updatedInitialState.copy(isLoading = true),
                awaitItem(),
            )
            assertEquals(
                updatedInitialState.copy(isLoading = false),
                awaitItem(),
            )
        }
        coVerify { userRemoteDataSource.login(testUsername, testPassword) }
        coVerify { userRemoteDataSource.updatePassword(newPassword) }
        verify { userLocalDataSource.storePassword(newPassword) }
        coVerify { userRemoteDataSource.login(testUsername, newPassword) }
        verify { userLocalDataSource.storeCredentials(AuthData(testUserId)) }
    }

    @Test
    fun `GIVEN new email and success response WHEN save THEN updates email`() = runTest {
        val newEmail = "new@email.com"
        viewModel.profileDataChanged(newEmail, testPassword)
        coEvery { userRemoteDataSource.login(any(), any()) } returns Result.success(testUserId)
        coEvery { userRemoteDataSource.updateEmail(any()) } returns Result.success(Unit)
        every { userLocalDataSource.storeLoginData(any(), any()) } just Runs

        viewModel.state.test {
            val updatedInitialState = initialState.copy(
                email = newEmail,
            )
            assertEquals(updatedInitialState, awaitItem())

            viewModel.save()

            assertEquals(
                updatedInitialState.copy(isLoading = true),
                awaitItem(),
            )
            assertEquals(
                updatedInitialState.copy(isLoading = false),
                awaitItem(),
            )
        }
        coVerify { userRemoteDataSource.login(testUsername, testPassword) }
        coVerify { userRemoteDataSource.updateEmail(newEmail) }
        verify {
            userLocalDataSource.storeLoginData(
                UserData(testUsername, newEmail, testPassword),
                AuthData(testUserId),
            )
        }
    }

    @Test
    fun `GIVEN new password and login failure WHEN save THEN show error`() = runTest {
        val newPassword = "123456"
        viewModel.profileDataChanged(testEmail, newPassword)
        coEvery { userRemoteDataSource.login(any(), any()) } returns Result.failure(Exception())

        viewModel.profileError.test {
            assertEquals(null, awaitItem())

            viewModel.save()

            assertEquals(
                ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server),
                awaitItem(),
            )
        }
        coVerify { userRemoteDataSource.login(testUsername, testPassword) }
        coVerify(exactly = 0) { userRemoteDataSource.updatePassword(newPassword) }
        verify(exactly = 0) { userLocalDataSource.storePassword(newPassword) }
        coVerify(exactly = 0) { userRemoteDataSource.login(testUsername, newPassword) }
        verify(exactly = 0) { userLocalDataSource.storeCredentials(AuthData(testUserId)) }
    }

    @Test
    fun `GIVEN new password and failure response WHEN save THEN show error`() = runTest {
        val newPassword = "123456"
        viewModel.profileDataChanged(testEmail, newPassword)
        coEvery { userRemoteDataSource.login(any(), any()) } returns Result.success(testUserId)
        coEvery { userRemoteDataSource.updatePassword(any()) } returns Result.failure(Exception())

        viewModel.profileError.test {
            assertEquals(null, awaitItem())

            viewModel.save()

            assertEquals(
                ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server),
                awaitItem(),
            )
        }
        coVerify { userRemoteDataSource.login(testUsername, testPassword) }
        coVerify { userRemoteDataSource.updatePassword(newPassword) }
        verify(exactly = 0) { userLocalDataSource.storePassword(newPassword) }
        coVerify(exactly = 0) { userRemoteDataSource.login(testUsername, newPassword) }
    }

    @Test
    fun `GIVEN same data WHEN save THEN do nothing`() {
        viewModel.save()

        coVerify(exactly = 0) { userRemoteDataSource.updateEmail(any()) }
        coVerify(exactly = 0) { userRemoteDataSource.updatePassword(any()) }
    }

    @Test
    fun `GIVEN true and success response WHEN setPublicProfile THEN register public profile`() =
        runTest {
            val value = true
            coEvery {
                userRemoteDataSource.registerPublicProfile(
                    any(),
                    any(),
                )
            } returns Result.success(Unit)
            every { userLocalDataSource.storePublicProfile(any()) } just Runs

            viewModel.state.test {
                assertEquals(initialState, awaitItem())

                viewModel.setPublicProfile(value)

                assertEquals(initialState.copy(isLoading = true), awaitItem())
                assertEquals(
                    initialState.copy(isProfilePublic = value, isLoading = false),
                    awaitItem(),
                )
            }
            coVerify { userRemoteDataSource.registerPublicProfile(testUsername, testUserId) }
            verify { userLocalDataSource.storePublicProfile(value) }
        }

    @Test
    fun `GIVEN false and success response WHEN setPublicProfile THEN delete public profile`() =
        runTest {
            val value = false
            coEvery { userRemoteDataSource.deletePublicProfile(any()) } returns Result.success(Unit)
            every { userLocalDataSource.storePublicProfile(any()) } just Runs

            viewModel.state.test {
                assertEquals(initialState, awaitItem())

                viewModel.setPublicProfile(value)

                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem(),
                )
                assertEquals(
                    initialState.copy(isProfilePublic = value, isLoading = false),
                    awaitItem(),
                )
            }
            coVerify { userRemoteDataSource.deletePublicProfile(testUserId) }
            verify { userLocalDataSource.storePublicProfile(value) }
        }

    @Test
    fun `GIVEN value and failure response WHEN setPublicProfile THEN show error`() = runTest {
        val value = true
        coEvery {
            userRemoteDataSource.registerPublicProfile(any(), any())
        } returns Result.failure(Exception())

        viewModel.profileError.test {
            assertEquals(null, awaitItem())

            viewModel.setPublicProfile(value)

            assertEquals(
                ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server),
                awaitItem(),
            )
        }
        coVerify { userRemoteDataSource.registerPublicProfile(testUsername, testUserId) }
    }

    @Test
    fun `GIVEN success response WHEN deleteUser THEN all books are removed and logs out`() =
        runTest {
            coEvery { userRemoteDataSource.login(any(), any()) } returns Result.success(testUserId)
            coEvery { userRemoteDataSource.deleteUser(any()) } returns Result.success(Unit)
            every { userLocalDataSource.logout() } just Runs
            every { userLocalDataSource.removeUserData() } just Runs
            val book = Book("id")
            every { booksLocalDataSource.getAllBooks() } returns flowOf(listOf(book))
            coEvery { booksLocalDataSource.deleteBooks(any()) } just Runs

            viewModel.logOut.test {
                assertEquals(false, awaitItem())

                viewModel.deleteUser()

                assertEquals(true, awaitItem())
            }
            verify { booksLocalDataSource.getAllBooks() }
            coVerify { booksLocalDataSource.deleteBooks(listOf(book)) }
            coVerify { userLocalDataSource.logout() }
            coVerify { userLocalDataSource.removeUserData() }
            coVerify { userRemoteDataSource.deleteUser(testUserId) }
            coVerify { userRemoteDataSource.login(testUsername, testPassword) }
        }

    @Test
    fun `GIVEN delete books failure response WHEN deleteUser THEN books are not removed and logs out`() =
        runTest {
            coEvery { userRemoteDataSource.login(any(), any()) } returns Result.success(testUserId)
            coEvery { userRemoteDataSource.deleteUser(any()) } returns Result.success(Unit)
            every { userLocalDataSource.logout() } just Runs
            every { userLocalDataSource.removeUserData() } just Runs
            every { booksLocalDataSource.getAllBooks() } returns flowOf(emptyList())
            coEvery { booksLocalDataSource.deleteBooks(any()) } throws Exception()

            viewModel.logOut.test {
                assertEquals(false, awaitItem())

                viewModel.deleteUser()

                assertEquals(true, awaitItem())
            }
            verify { booksLocalDataSource.getAllBooks() }
            coVerify { booksLocalDataSource.deleteBooks(any()) }
            coVerify { userLocalDataSource.logout() }
            coVerify { userLocalDataSource.removeUserData() }
            coVerify { userRemoteDataSource.deleteUser(testUserId) }
            coVerify { userRemoteDataSource.login(testUsername, testPassword) }
        }

    @Test
    fun `GIVEN delete user failure response WHEN deleteUser THEN show error`() = runTest {
        coEvery { userRemoteDataSource.login(any(), any()) } returns Result.success(testUserId)
        coEvery { userRemoteDataSource.deleteUser(any()) } returns Result.failure(Exception())

        viewModel.profileError.test {
            assertEquals(null, awaitItem())

            viewModel.deleteUser()

            assertEquals(
                ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server),
                awaitItem(),
            )
        }
        coVerify { userRemoteDataSource.deleteUser(testUserId) }
        coVerify { userRemoteDataSource.login(testUsername, testPassword) }
    }

    @Test
    fun `GIVEN valid data WHEN profileDataChanged THEN update state with new data`() = runTest {
        val newEmail = "new@email.com"
        val newPassword = "123456"

        viewModel.state.test {
            assertEquals(initialState, awaitItem())

            viewModel.profileDataChanged(newEmail, newPassword)

            assertEquals(
                initialState.copy(
                    email = newEmail,
                    emailError = null,
                    password = newPassword,
                    passwordError = null,
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `GIVEN invalid data WHEN profileDataChanged THEN update state with errors`() = runTest {
        val newEmail = "invalid-email"
        val newPassword = "123"

        viewModel.state.test {
            assertEquals(initialState, awaitItem())

            viewModel.profileDataChanged(newEmail, newPassword)

            assertEquals(
                initialState.copy(
                    email = newEmail,
                    emailError = Res.string.invalid_email,
                    password = newPassword,
                    passwordError = Res.string.invalid_password,
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `GIVEN no dialog shown WHEN showConfirmationDialog THEN dialog is shown`() = runTest {
        viewModel.confirmationDialogMessageId.test {
            assertEquals(null, awaitItem())

            viewModel.showConfirmationDialog(Res.string.profile_delete_confirmation)

            assertEquals(Res.string.profile_delete_confirmation, awaitItem())
        }
    }

    @Test
    fun `GIVEN same dialog message shown WHEN showConfirmationDialog THEN do nothing`() = runTest {
        viewModel.confirmationDialogMessageId.test {
            assertEquals(null, awaitItem())
            viewModel.showConfirmationDialog(Res.string.profile_delete_confirmation)
            assertEquals(Res.string.profile_delete_confirmation, awaitItem())

            viewModel.showConfirmationDialog(Res.string.profile_delete_confirmation)

            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN no dialog shown WHEN showInfoDialog THEN dialog is shown`() = runTest {
        viewModel.infoDialogMessageId.test {
            assertEquals(null, awaitItem())

            viewModel.showInfoDialog(Res.string.username_info)

            assertEquals(Res.string.username_info, awaitItem())
        }
    }

    @Test
    fun `GIVEN same dialog message shown WHEN showInfoDialog THEN do nothing`() = runTest {
        viewModel.infoDialogMessageId.test {
            assertEquals(null, awaitItem())
            viewModel.showInfoDialog(Res.string.username_info)
            assertEquals(Res.string.username_info, awaitItem())

            viewModel.showInfoDialog(Res.string.username_info)

            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN dialog shown WHEN closeDialogs THEN dialog is reset`() = runTest {
        viewModel.confirmationDialogMessageId.test {
            val confirmationDialogMessage = this
            assertEquals(null, awaitItem())
            viewModel.showConfirmationDialog(Res.string.profile_delete_confirmation)
            assertEquals(
                Res.string.profile_delete_confirmation,
                confirmationDialogMessage.awaitItem(),
            )

            viewModel.infoDialogMessageId.test {
                val infoDialogMessage = this
                assertEquals(null, awaitItem())
                viewModel.showInfoDialog(Res.string.username_info)
                assertEquals(
                    Res.string.username_info,
                    infoDialogMessage.awaitItem(),
                )

                viewModel.profileError.test {
                    val profileError = this
                    assertEquals(null, awaitItem())
                    coEvery {
                        userRemoteDataSource.login(
                            any(),
                            any(),
                        )
                    } returns Result.success(testUserId)
                    coEvery {
                        userRemoteDataSource.deleteUser(
                            any(),
                        )
                    } returns Result.failure(Exception())
                    viewModel.deleteUser()
                    assertEquals(
                        ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server),
                        awaitItem(),
                    )

                    viewModel.closeDialogs()

                    assertEquals(null, confirmationDialogMessage.awaitItem())
                    assertEquals(null, infoDialogMessage.awaitItem())
                    assertEquals(null, profileError.awaitItem())
                }
            }
        }
    }

    @Test
    fun `GIVEN no dialog shown WHEN closeDialogs THEN do nothing`() = runTest {
        viewModel.confirmationDialogMessageId.test {
            assertEquals(null, awaitItem())

            viewModel.infoDialogMessageId.test {
                assertEquals(null, awaitItem())

                viewModel.profileError.test {
                    assertEquals(null, awaitItem())

                    viewModel.closeDialogs()

                    expectNoEvents()
                }
            }
        }
    }
}