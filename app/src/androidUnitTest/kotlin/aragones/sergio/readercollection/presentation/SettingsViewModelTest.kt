/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 5/10/2025
 */

@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("ktlint:standard:max-line-length")

package aragones.sergio.readercollection.presentation

import app.cash.turbine.test
import aragones.sergio.readercollection.data.BooksRepositoryImpl
import aragones.sergio.readercollection.data.UserRepositoryImpl
import aragones.sergio.readercollection.data.local.BooksLocalDataSource
import aragones.sergio.readercollection.data.local.UserLocalDataSource
import aragones.sergio.readercollection.data.remote.BooksRemoteDataSource
import aragones.sergio.readercollection.data.remote.UserRemoteDataSource
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.domain.toRemoteData
import aragones.sergio.readercollection.presentation.settings.SettingsUiState
import aragones.sergio.readercollection.presentation.settings.SettingsViewModel
import aragones.sergio.readercollection.presentation.utils.MainDispatcherRule
import com.aragones.sergio.util.Constants
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
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
import reader_collection.app.generated.resources.data_sync_successfully
import reader_collection.app.generated.resources.error_server
import reader_collection.app.generated.resources.export_confirmation
import reader_collection.app.generated.resources.profile_logout_confirmation

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testUserId = "userId"
    private val testUsername = "username"
    private val booksLocalDataSource: BooksLocalDataSource = mockk {
        every { retrieveRemoteConfigValues() } just Runs
    }
    private val booksRemoteDataSource: BooksRemoteDataSource = mockk()
    private val userLocalDataSource: UserLocalDataSource = mockk {
        every { isAutomaticSyncEnabled } returns false
        every { userId } returns testUserId
        every { username } returns testUsername
    }
    private val userRemoteDataSource: UserRemoteDataSource = mockk()
    private val viewModel = SettingsViewModel(
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

    @Test
    fun `GIVEN version WHEN onResume THEN state is updated with version`() = runTest {
        val version = "1.2.3"
        val language = "en"
        val sortParam = "title"
        val isSortDescending = true
        val themeMode = 1
        every { userLocalDataSource.isProfilePublic } returns false
        every { userLocalDataSource.getCurrentVersion() } returns version
        every { userLocalDataSource.isAutomaticSyncEnabled } returns false
        every { userLocalDataSource.language } returns language
        every { userLocalDataSource.sortParam } returns sortParam
        every { userLocalDataSource.isSortDescending } returns isSortDescending
        every { userLocalDataSource.themeMode } returns themeMode

        viewModel.state.test {
            assertEquals(SettingsUiState.empty(), awaitItem())

            viewModel.onResume()

            assertEquals(
                SettingsUiState.empty().copy(
                    isProfilePublic = false,
                    isAutomaticSyncEnabled = false,
                    version = version,
                    language = language,
                    sortParam = sortParam,
                    isSortDescending = isSortDescending,
                    themeMode = themeMode,
                ),
                awaitItem(),
            )
        }
        verify { userLocalDataSource.isProfilePublic }
        verify { userLocalDataSource.isAutomaticSyncEnabled }
        verify { userLocalDataSource.language }
        verify { userLocalDataSource.sortParam }
        verify { userLocalDataSource.isSortDescending }
        verify { userLocalDataSource.themeMode }
        verify { userLocalDataSource.getCurrentVersion() }
        verify { booksLocalDataSource.retrieveRemoteConfigValues() }
        confirmVerified(booksLocalDataSource, userLocalDataSource)
    }

    @Test
    fun `GIVEN true and success response WHEN setPublicProfile THEN register public profile`() =
        runTest {
            val value = true
            coEvery {
                userRemoteDataSource.registerPublicProfile(any(), any())
            } returns Result.success(Unit)
            every { userLocalDataSource.storePublicProfile(any()) } just Runs

            viewModel.state.test {
                assertEquals(SettingsUiState.empty(), awaitItem())

                viewModel.setPublicProfile(value)

                assertEquals(
                    SettingsUiState.empty().copy(isLoading = true),
                    awaitItem(),
                )
                assertEquals(
                    SettingsUiState.empty().copy(
                        isProfilePublic = value,
                        isLoading = false,
                    ),
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
                assertEquals(SettingsUiState.empty(), awaitItem())

                viewModel.setPublicProfile(value)

                assertEquals(
                    SettingsUiState.empty().copy(isLoading = true),
                    awaitItem(),
                )
                assertEquals(
                    SettingsUiState.empty().copy(
                        isProfilePublic = value,
                        isLoading = false,
                    ),
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

        viewModel.state.test {
            val state = this
            assertEquals(SettingsUiState.empty(), awaitItem())

            viewModel.error.test {
                assertEquals(null, awaitItem())

                viewModel.setPublicProfile(value)

                assertEquals(
                    SettingsUiState.empty().copy(isLoading = true),
                    state.awaitItem(),
                )
                assertEquals(
                    SettingsUiState.empty().copy(isLoading = false),
                    state.awaitItem(),
                )
                assertEquals(
                    ErrorModel(Constants.EMPTY_VALUE, Res.string.error_server),
                    awaitItem(),
                )
            }
        }
        coVerify { userRemoteDataSource.registerPublicProfile(testUsername, testUserId) }
    }

    @Test
    fun `GIVEN value WHEN changeAutomaticSync THEN value is updated`() = runTest {
        val value = false
        every { userLocalDataSource.storeAutomaticSync(any()) } just Runs

        viewModel.state.test {
            assertEquals(SettingsUiState.empty(), awaitItem())

            viewModel.changeAutomaticSync(value)

            assertEquals(
                SettingsUiState.empty().copy(
                    isAutomaticSyncEnabled = value,
                ),
                awaitItem(),
            )
        }
        verify { userLocalDataSource.storeAutomaticSync(value) }
        confirmVerified(userLocalDataSource)
    }

    @Test
    fun `GIVEN new books and out-of-date books WHEN syncData THEN add new books and remove out-of-date books and show success message`() =
        runTest {
            val newBook = Book("bookId1")
            val outOfDateBook = Book("bookId2")
            coEvery {
                booksRemoteDataSource.getBooks(any())
            } returns Result.success(listOf(outOfDateBook.toRemoteData()))
            coEvery { booksLocalDataSource.getAllBooks() } returns flowOf(listOf(newBook))
            coEvery {
                booksRemoteDataSource.syncBooks(any(), any(), any())
            } returns Result.success(Unit)

            viewModel.infoDialogMessageId.test {
                assertEquals(null, awaitItem())

                viewModel.syncData()

                assertEquals(Res.string.data_sync_successfully, awaitItem())
            }
            verify { userLocalDataSource.userId }
            coVerify { booksRemoteDataSource.getBooks(any()) }
            coVerify { booksLocalDataSource.getAllBooks() }
            coVerify {
                booksRemoteDataSource.syncBooks(
                    uuid = testUserId,
                    booksToSave = listOf(newBook.toRemoteData()),
                    booksToRemove = listOf(outOfDateBook.toRemoteData()),
                )
            }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(booksLocalDataSource, booksRemoteDataSource, userLocalDataSource)
        }

    @Test
    fun `GIVEN new books but no out-of-date books WHEN syncData THEN add just new books and show success message`() =
        runTest {
            val newBook = Book("bookId")
            coEvery { booksRemoteDataSource.getBooks(any()) } returns Result.success(emptyList())
            coEvery { booksLocalDataSource.getAllBooks() } returns flowOf(listOf(newBook))
            coEvery {
                booksRemoteDataSource.syncBooks(any(), any(), any())
            } returns Result.success(Unit)

            viewModel.infoDialogMessageId.test {
                assertEquals(null, awaitItem())

                viewModel.syncData()

                assertEquals(Res.string.data_sync_successfully, awaitItem())
            }
            verify { userLocalDataSource.userId }
            coVerify { booksRemoteDataSource.getBooks(any()) }
            coVerify { booksLocalDataSource.getAllBooks() }
            coVerify {
                booksRemoteDataSource.syncBooks(
                    uuid = testUserId,
                    booksToSave = listOf(newBook.toRemoteData()),
                    booksToRemove = emptyList(),
                )
            }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(booksLocalDataSource, booksRemoteDataSource, userLocalDataSource)
        }

    @Test
    fun `GIVEN no new books and out-of-date books WHEN syncData THEN remove just out-of-date books and show success message`() =
        runTest {
            val outOfDateBook = Book("bookId")
            coEvery {
                booksRemoteDataSource.getBooks(any())
            } returns Result.success(listOf(outOfDateBook.toRemoteData()))
            coEvery { booksLocalDataSource.getAllBooks() } returns flowOf(emptyList())
            coEvery {
                booksRemoteDataSource.syncBooks(any(), any(), any())
            } returns Result.success(Unit)

            viewModel.infoDialogMessageId.test {
                assertEquals(null, awaitItem())

                viewModel.syncData()

                assertEquals(Res.string.data_sync_successfully, awaitItem())
            }
            verify { userLocalDataSource.userId }
            coVerify { booksRemoteDataSource.getBooks(any()) }
            coVerify { booksLocalDataSource.getAllBooks() }
            coVerify {
                booksRemoteDataSource.syncBooks(
                    uuid = testUserId,
                    booksToSave = emptyList(),
                    booksToRemove = listOf(outOfDateBook.toRemoteData()),
                )
            }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(booksLocalDataSource, booksRemoteDataSource, userLocalDataSource)
        }

    @Test
    fun `GIVEN failure WHEN syncData THEN show error message`() = runTest {
        coEvery { booksRemoteDataSource.getBooks(any()) } returns Result.success(emptyList())
        coEvery { booksLocalDataSource.getAllBooks() } returns flowOf(emptyList())
        coEvery {
            booksRemoteDataSource.syncBooks(any(), any(), any())
        } returns Result.failure(Exception())

        viewModel.error.test {
            assertEquals(null, awaitItem())

            viewModel.syncData()

            assertEquals(
                ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_server,
                ),
                awaitItem(),
            )
        }
        verify { userLocalDataSource.userId }
        coVerify { booksRemoteDataSource.getBooks(any()) }
        coVerify { booksLocalDataSource.getAllBooks() }
        coVerify {
            booksRemoteDataSource.syncBooks(
                uuid = testUserId,
                booksToSave = emptyList(),
                booksToRemove = emptyList(),
            )
        }
        verify { booksLocalDataSource.retrieveRemoteConfigValues() }
        confirmVerified(booksLocalDataSource, booksRemoteDataSource, userLocalDataSource)
    }

    @Test
    fun `GIVEN new language WHEN displaySettingsChanged THEN repository is called and relaunch is not triggered`() =
        runTest {
            val newLanguage = "es"
            every { userLocalDataSource.storeLanguage(any()) } just Runs
            every { userLocalDataSource.language } returns "en"
            every { userLocalDataSource.sortParam } returns "title"
            every { userLocalDataSource.isSortDescending } returns true
            every { userLocalDataSource.themeMode } returns 0

            viewModel.relaunch.test {
                assertEquals(false, awaitItem())

                viewModel.displaySettingsChanged(
                    newLanguage = newLanguage,
                    newSortParam = "title",
                    newIsSortDescending = true,
                    newThemeMode = 0,
                )

                expectNoEvents()
            }

            verify { userLocalDataSource.language }
            verify { userLocalDataSource.sortParam }
            verify { userLocalDataSource.isSortDescending }
            verify { userLocalDataSource.themeMode }
            verify { userLocalDataSource.storeLanguage(newLanguage) }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(booksLocalDataSource, userLocalDataSource)
        }

    @Test
    fun `GIVEN new sort parameters WHEN displaySettingsChanged THEN repository is called and relaunch is triggered`() =
        runTest {
            val newSortParam = "author"
            val newIsSortDescending = false
            every { userLocalDataSource.storeSortParam(any()) } just Runs
            every { userLocalDataSource.storeIsSortDescending(any()) } just Runs
            every { userLocalDataSource.language } returns "en"
            every { userLocalDataSource.sortParam } returns "title"
            every { userLocalDataSource.isSortDescending } returns true
            every { userLocalDataSource.themeMode } returns 0

            viewModel.relaunch.test {
                assertEquals(false, awaitItem())

                viewModel.displaySettingsChanged(
                    newLanguage = "en",
                    newSortParam = newSortParam,
                    newIsSortDescending = newIsSortDescending,
                    newThemeMode = 0,
                )

                assertEquals(true, awaitItem())
            }

            verify { userLocalDataSource.language }
            verify { userLocalDataSource.sortParam }
            verify { userLocalDataSource.isSortDescending }
            verify { userLocalDataSource.themeMode }
            verify { userLocalDataSource.storeSortParam(newSortParam) }
            verify { userLocalDataSource.storeIsSortDescending(newIsSortDescending) }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(booksLocalDataSource, userLocalDataSource)
        }

    @Test
    fun `GIVEN new theme mode WHEN displaySettingsChanged THEN repository is called and relaunch is triggered`() =
        runTest {
            val newThemeMode = 2
            every { userLocalDataSource.storeThemeMode(any()) } just Runs
            every { userLocalDataSource.applyTheme() } just Runs
            every { userLocalDataSource.language } returns "en"
            every { userLocalDataSource.sortParam } returns "title"
            every { userLocalDataSource.isSortDescending } returns true
            every { userLocalDataSource.themeMode } returns 0

            viewModel.relaunch.test {
                assertEquals(false, awaitItem())

                viewModel.displaySettingsChanged(
                    newLanguage = "en",
                    newSortParam = "title",
                    newIsSortDescending = true,
                    newThemeMode = newThemeMode,
                )

                assertEquals(true, awaitItem())
            }

            verify { userLocalDataSource.language }
            verify { userLocalDataSource.sortParam }
            verify { userLocalDataSource.isSortDescending }
            verify { userLocalDataSource.themeMode }
            verify { userLocalDataSource.storeThemeMode(newThemeMode) }
            verify { userLocalDataSource.applyTheme() }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(booksLocalDataSource, userLocalDataSource)
        }

    @Test
    fun `GIVEN no changes WHEN displaySettingsChanged THEN repository is not called and relaunch is not triggered`() =
        runTest {
            every { userLocalDataSource.language } returns "en"
            every { userLocalDataSource.sortParam } returns "title"
            every { userLocalDataSource.isSortDescending } returns true
            every { userLocalDataSource.themeMode } returns 0

            viewModel.relaunch.test {
                assertEquals(false, awaitItem())

                viewModel.displaySettingsChanged(
                    newLanguage = "en",
                    newSortParam = "title",
                    newIsSortDescending = true,
                    newThemeMode = 0,
                )

                expectNoEvents()
            }

            verify { userLocalDataSource.language }
            verify { userLocalDataSource.sortParam }
            verify { userLocalDataSource.isSortDescending }
            verify { userLocalDataSource.themeMode }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(booksLocalDataSource, userLocalDataSource)
        }

    @Test
    fun `WHEN logout THEN data sources are invoked and logOut state returns true`() = runTest {
        val books = listOf(
            Book(id = "bookId1"),
            Book(id = "bookId2"),
        )
        every { userLocalDataSource.logout() } just Runs
        every { userRemoteDataSource.logout() } just Runs
        every { booksLocalDataSource.getAllBooks() } returns flowOf(books)
        coEvery { booksLocalDataSource.deleteBooks(any()) } just Runs
        viewModel.logOut.test {
            assertEquals(false, awaitItem())

            viewModel.logout()

            assertEquals(true, awaitItem())
        }
        verify { userLocalDataSource.logout() }
        verify { userRemoteDataSource.logout() }
        verify { booksLocalDataSource.getAllBooks() }
        coVerify { booksLocalDataSource.deleteBooks(books) }
        verify { booksLocalDataSource.retrieveRemoteConfigValues() }
        confirmVerified(booksLocalDataSource, userLocalDataSource, userRemoteDataSource)
    }

    @Test
    fun `GIVEN error on reset database WHEN logout THEN data sources are invoked and logOut state returns true`() =
        runTest {
            val books = listOf(
                Book(id = "bookId1"),
                Book(id = "bookId2"),
            )
            every { userLocalDataSource.logout() } just Runs
            every { userRemoteDataSource.logout() } just Runs
            every { booksLocalDataSource.getAllBooks() } returns flowOf(books)
            coEvery {
                booksLocalDataSource.deleteBooks(any())
            } throws RuntimeException("Database error")
            viewModel.logOut.test {
                assertEquals(false, awaitItem())

                viewModel.logout()

                assertEquals(true, awaitItem())
            }
            verify { userLocalDataSource.logout() }
            verify { userRemoteDataSource.logout() }
            verify { booksLocalDataSource.getAllBooks() }
            coVerify { booksLocalDataSource.deleteBooks(books) }
            verify { booksLocalDataSource.retrieveRemoteConfigValues() }
            confirmVerified(userLocalDataSource, userRemoteDataSource, booksLocalDataSource)
        }

    @Test
    fun `GIVEN no dialog shown WHEN showConfirmationDialog THEN dialog is shown`() = runTest {
        viewModel.confirmationDialogMessageId.test {
            assertEquals(null, awaitItem())

            viewModel.showConfirmationDialog(Res.string.profile_logout_confirmation)

            assertEquals(Res.string.profile_logout_confirmation, awaitItem())
        }
    }

    @Test
    fun `GIVEN same dialog message shown WHEN showConfirmationDialog THEN do nothing`() = runTest {
        viewModel.confirmationDialogMessageId.test {
            assertEquals(null, awaitItem())
            viewModel.showConfirmationDialog(Res.string.profile_logout_confirmation)
            assertEquals(Res.string.profile_logout_confirmation, awaitItem())

            viewModel.showConfirmationDialog(Res.string.profile_logout_confirmation)

            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN dialog shown WHEN closeDialogs THEN dialog is reset`() = runTest {
        coEvery { booksRemoteDataSource.getBooks(any()) } returns Result.success(emptyList())
        coEvery { booksLocalDataSource.getAllBooks() } returns flowOf(emptyList())
        coEvery {
            booksRemoteDataSource.syncBooks(any(), any(), any())
        } returns Result.success(Unit)
        every { userLocalDataSource.language } returns "en"

        viewModel.infoDialogMessageId.test {
            val infoDialogMessage = this
            assertEquals(null, awaitItem())

            viewModel.syncData()
            assertEquals(
                Res.string.data_sync_successfully,
                infoDialogMessage.awaitItem(),
            )

            viewModel.confirmationDialogMessageId.test {
                val confirmationDialogMessage = this
                assertEquals(null, awaitItem())
                viewModel.showConfirmationDialog(Res.string.export_confirmation)
                assertEquals(
                    Res.string.export_confirmation,
                    confirmationDialogMessage.awaitItem(),
                )
                viewModel.error.test {
                    val error = this
                    assertEquals(null, awaitItem())
                    coEvery {
                        booksRemoteDataSource.syncBooks(any(), any(), any())
                    } returns Result.failure(Exception())
                    viewModel.syncData()
                    assertEquals(
                        ErrorModel(
                            Constants.EMPTY_VALUE,
                            Res.string.error_server,
                        ),
                        awaitItem(),
                    )

                    viewModel.closeDialogs()

                    assertEquals(null, infoDialogMessage.awaitItem())
                    assertEquals(null, confirmationDialogMessage.awaitItem())
                    assertEquals(null, error.awaitItem())
                }
            }
        }
    }

    @Test
    fun `GIVEN no dialog shown WHEN closeDialogs THEN do nothing`() = runTest {
        viewModel.infoDialogMessageId.test {
            assertEquals(null, awaitItem())

            viewModel.confirmationDialogMessageId.test {
                assertEquals(null, awaitItem())

                viewModel.error.test {
                    assertEquals(null, awaitItem())

                    viewModel.closeDialogs()

                    expectNoEvents()
                }
            }
        }
    }
}