/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 26/9/2025
 */

@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("ktlint:standard:max-line-length")

package aragones.sergio.readercollection.presentation

import app.cash.turbine.test
import aragones.sergio.readercollection.data.BooksRepositoryImpl
import aragones.sergio.readercollection.data.local.BooksLocalDataSource
import aragones.sergio.readercollection.data.remote.BooksRemoteDataSource
import aragones.sergio.readercollection.data.remote.model.GoogleBookListResponse
import aragones.sergio.readercollection.data.remote.model.GoogleBookResponse
import aragones.sergio.readercollection.data.remote.model.GoogleVolumeResponse
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.Books
import aragones.sergio.readercollection.domain.toDomain
import aragones.sergio.readercollection.presentation.search.SearchParam
import aragones.sergio.readercollection.presentation.search.SearchUiState
import aragones.sergio.readercollection.presentation.search.SearchViewModel
import aragones.sergio.readercollection.presentation.utils.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val booksLocalDataSource: BooksLocalDataSource = mockk {
        every { retrieveRemoteConfigValues() } just Runs
    }
    private val booksRemoteDataSource: BooksRemoteDataSource = mockk()

    private val viewModel = SearchViewModel(
        BooksRepositoryImpl(
            booksLocalDataSource,
            booksRemoteDataSource,
            mainDispatcherRule.testDispatcher,
        ),
    )

    @Test
    fun `GIVEN success response WHEN searchBooks THEN return Success state with searched books`() =
        runTest {
            val bookId = "bookId"
            givenSearchBooksSuccess(bookId)
            val expectedBooks = listOf(
                getDefaultGoogleBook(bookId).toDomain(),
                Book(""),
            )

            viewModel.state.test {
                assertEquals(SearchUiState.Empty, awaitItem())

                viewModel.searchBooks()

                assertEquals(
                    SearchUiState.Success(
                        isLoading = true,
                        query = "",
                        books = Books(),
                        param = SearchParam.TITLE,
                    ),
                    awaitItem(),
                )
                assertEquals(
                    SearchUiState.Success(
                        isLoading = false,
                        query = "",
                        books = Books(expectedBooks),
                        param = SearchParam.TITLE,
                    ),
                    awaitItem(),
                )
            }
            coVerify {
                booksRemoteDataSource.searchBooks(
                    query = "",
                    filter = "intitle",
                    page = 1,
                    order = null,
                )
            }
            confirmVerified(booksRemoteDataSource)
        }

    @Test
    fun `GIVEN empty response WHEN searchBooks THEN return Success state with empty list`() =
        runTest {
            coEvery {
                booksRemoteDataSource.searchBooks(any(), any(), any(), any())
            } returns Result.success(
                GoogleBookListResponse(
                    totalItems = 0,
                    items = emptyList(),
                ),
            )

            viewModel.state.test {
                assertEquals(SearchUiState.Empty, awaitItem())

                viewModel.searchBooks()

                assertEquals(
                    SearchUiState.Success(
                        isLoading = true,
                        query = "",
                        books = Books(),
                        param = SearchParam.TITLE,
                    ),
                    awaitItem(),
                )
                assertEquals(
                    SearchUiState.Success(
                        isLoading = false,
                        query = "",
                        books = Books(),
                        param = SearchParam.TITLE,
                    ),
                    awaitItem(),
                )
            }
            coVerify {
                booksRemoteDataSource.searchBooks(
                    query = "",
                    filter = "intitle",
                    page = 1,
                    order = null,
                )
            }
            confirmVerified(booksRemoteDataSource)
        }

    @Test
    fun `GIVEN failure response WHEN searchBooks THEN return Success state with empty list`() =
        runTest {
            coEvery {
                booksRemoteDataSource.searchBooks(any(), any(), any(), any())
            } returns Result.failure(RuntimeException("Firestore error"))

            viewModel.state.test {
                assertEquals(SearchUiState.Empty, awaitItem())

                viewModel.searchBooks()

                assertEquals(
                    SearchUiState.Success(
                        isLoading = true,
                        query = "",
                        books = Books(),
                        param = SearchParam.TITLE,
                    ),
                    awaitItem(),
                )
                assertEquals(
                    SearchUiState.Success(
                        isLoading = false,
                        query = "",
                        books = Books(),
                        param = SearchParam.TITLE,
                    ),
                    awaitItem(),
                )
            }
            coVerify {
                booksRemoteDataSource.searchBooks(
                    query = "",
                    filter = "intitle",
                    page = 1,
                    order = null,
                )
            }
            confirmVerified(booksRemoteDataSource)
        }

    @Test
    fun `GIVEN reload WHEN searchBooks THEN page and books are reset`() = runTest {
        val bookId = "bookId"
        givenSearchBooksSuccess(bookId)

        viewModel.state.test {
            assertEquals(SearchUiState.Empty, awaitItem())

            viewModel.searchBooks()

            assertEquals(
                SearchUiState.Success(
                    isLoading = true,
                    query = "",
                    books = Books(),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )
            assertEquals(
                SearchUiState.Success(
                    isLoading = false,
                    query = "",
                    books = Books(
                        listOf(
                            getDefaultGoogleBook(bookId).toDomain(),
                            Book(""),
                        ),
                    ),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )

            viewModel.searchBooks()

            assertEquals(
                SearchUiState.Success(
                    isLoading = true,
                    query = "",
                    books = Books(
                        listOf(
                            getDefaultGoogleBook(bookId).toDomain(),
                            Book(""),
                        ),
                    ),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )
            assertEquals(
                SearchUiState.Success(
                    isLoading = false,
                    query = "",
                    books = Books(
                        listOf(
                            getDefaultGoogleBook(bookId).toDomain(),
                            getDefaultGoogleBook(bookId).toDomain(),
                            Book(""),
                        ),
                    ),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )

            viewModel.searchBooks(reload = true)

            assertEquals(
                SearchUiState.Success(
                    isLoading = true,
                    query = "",
                    books = Books(
                        listOf(
                            getDefaultGoogleBook(bookId).toDomain(),
                            getDefaultGoogleBook(bookId).toDomain(),
                            Book(""),
                        ),
                    ),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )
            assertEquals(
                SearchUiState.Success(
                    isLoading = false,
                    query = "",
                    books = Books(
                        listOf(
                            getDefaultGoogleBook(bookId).toDomain(),
                            Book(""),
                        ),
                    ),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )
        }
        coVerify(exactly = 2) {
            booksRemoteDataSource.searchBooks(
                query = "",
                filter = "intitle",
                page = 1,
                order = null,
            )
        }
        coVerify {
            booksRemoteDataSource.searchBooks(
                query = "",
                filter = "intitle",
                page = 2,
                order = null,
            )
        }
        confirmVerified(booksRemoteDataSource)
    }

    @Test
    fun `GIVEN new query WHEN searchBooks THEN query is updated`() = runTest {
        val bookId = "bookId"
        givenSearchBooksSuccess(bookId)
        val expectedBooks = listOf(
            getDefaultGoogleBook(bookId).toDomain(),
            Book(""),
        )
        val query = "text to search"

        viewModel.state.test {
            assertEquals(SearchUiState.Empty, awaitItem())

            viewModel.searchBooks(query = query)

            assertEquals(
                SearchUiState.Success(
                    isLoading = true,
                    query = query,
                    books = Books(),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )
            assertEquals(
                SearchUiState.Success(
                    isLoading = false,
                    query = query,
                    books = Books(expectedBooks),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )
        }
        coVerify {
            booksRemoteDataSource.searchBooks(
                query = query,
                filter = "intitle",
                page = 1,
                order = null,
            )
        }
        confirmVerified(booksRemoteDataSource)
    }

    @Test
    fun `GIVEN new param WHEN changeFilter THEN update state with new param`() = runTest {
        viewModel.state.test {
            assertEquals(SearchUiState.Empty, awaitItem())

            viewModel.changeFilter(SearchParam.TITLE)

            assertEquals(
                SearchUiState.Success(
                    isLoading = false,
                    query = "",
                    books = Books(),
                    param = SearchParam.TITLE,
                ),
                awaitItem(),
            )
        }
    }

    private fun getDefaultGoogleBook(bookId: String) = GoogleBookResponse(
        id = bookId,
        volumeInfo = GoogleVolumeResponse(
            title = "",
            subtitle = "",
            authors = listOf(),
            publisher = "",
            publishedDate = null,
            description = "",
            industryIdentifiers = listOf(),
            pageCount = 0,
            categories = listOf(),
            averageRating = 0.0,
            ratingsCount = 0,
            imageLinks = null,
        ),
    )

    private fun givenSearchBooksSuccess(bookId: String) {
        val response = GoogleBookListResponse(
            totalItems = 1,
            items = listOf(getDefaultGoogleBook(bookId)),
        )
        coEvery {
            booksRemoteDataSource.searchBooks(any(), any(), any(), any())
        } returns Result.success(response)
    }
}