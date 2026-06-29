/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 11/11/2020
 */

package aragones.sergio.readercollection.presentation.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import aragones.sergio.readercollection.domain.BooksRepository
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.presentation.navigation.Route
import com.aragones.sergio.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.book_removed
import reader_collection.app.generated.resources.book_saved
import reader_collection.app.generated.resources.error_database
import reader_collection.app.generated.resources.error_no_book

class BookDetailViewModel(
    state: SavedStateHandle,
    private val booksRepository: BooksRepository,
) : ViewModel() {

    //region Private properties
    private val params = state.toRoute<Route.BookDetail>()
    private lateinit var currentBook: Book
    private lateinit var savedBooks: List<Book>
    val state: StateFlow<BookDetailUiState>
        field = MutableStateFlow<BookDetailUiState>(
            BookDetailUiState(
                book = null,
                isAlreadySaved = true,
                isEditable = false,
            ),
        )
    val bookDetailError: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    val confirmationDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    val imageDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    private val pendingBooks: List<Book>
        get() = savedBooks.filter { it.isPending() }
    //endregion

    //region Lifecycle methods
    fun onCreate() {
        viewModelScope.launch {
            booksRepository.getBooks().collect {
                savedBooks = it
            }
        }
        fetchBook()
    }
    //endregion

    //region Public methods
    fun enableEdition() {
        state.update { it.copy(isEditable = true) }
    }

    fun disableEdition() {
        state.update {
            it.copy(
                book = currentBook,
                isEditable = false,
            )
        }
    }

    fun changeData(book: Book) {
        state.update { it.copy(book = book) }
    }

    fun createBook(newBook: Book) = viewModelScope.launch {
        val maxPriority = (pendingBooks.maxByOrNull { it.priority }?.priority ?: -1)
        val book = newBook.copy(priority = maxPriority + 1)
        booksRepository.createBook(book).fold(
            onSuccess = {
                infoDialogMessageId.value = Res.string.book_saved
                state.update {
                    it.copy(
                        book = book,
                        isAlreadySaved = true,
                        isEditable = false,
                    )
                }
            },
            onFailure = {
                bookDetailError.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_database,
                )
            },
        )
    }

    fun setBook(book: Book) = viewModelScope.launch {
        booksRepository.setBook(book).fold(
            onSuccess = { updatedBook ->
                currentBook = updatedBook
                state.update {
                    it.copy(
                        book = updatedBook,
                        isEditable = false,
                    )
                }
            },
            onFailure = {
                bookDetailError.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_database,
                )
            },
        )
    }

    fun deleteBook() = viewModelScope.launch {
        booksRepository.deleteBook(params.bookId).fold(
            onSuccess = {
                infoDialogMessageId.value = Res.string.book_removed
                state.update {
                    it.copy(
                        isAlreadySaved = false,
                        isEditable = true,
                    )
                }
            },
            onFailure = {
                bookDetailError.value = ErrorModel(
                    Constants.EMPTY_VALUE,
                    Res.string.error_database,
                )
            },
        )
    }

    fun setBookImage(imageUri: String?) {
        state.update {
            it.copy(
                book = state.value.book?.copy(
                    thumbnail = imageUri,
                ),
            )
        }
    }

    fun showConfirmationDialog(textId: StringResource) {
        confirmationDialogMessageId.value = textId
    }

    fun showImageDialog(textId: StringResource) {
        imageDialogMessageId.value = textId
    }

    fun closeDialogs() {
        confirmationDialogMessageId.value = null
        infoDialogMessageId.value = null
        imageDialogMessageId.value = null
    }
    //endregion

    //region Private methods
    private fun fetchBook() = viewModelScope.launch {
        if (params.friendId.isNotEmpty()) {
            booksRepository.getFriendBook(params.friendId, params.bookId).fold(
                onSuccess = { book ->
                    currentBook = book
                    state.update {
                        it.copy(
                            book = book,
                            isEditable = true,
                            isAlreadySaved = false,
                        )
                    }
                },
                onFailure = {
                    bookDetailError.value = ErrorModel("", Res.string.error_no_book)
                },
            )
        } else {
            booksRepository.getBook(params.bookId).fold(
                onSuccess = { (book, isAlreadySaved) ->
                    currentBook = book
                    state.update {
                        it.copy(
                            book = book,
                            isEditable = !isAlreadySaved,
                            isAlreadySaved = isAlreadySaved,
                        )
                    }
                },
                onFailure = {
                    bookDetailError.value = ErrorModel("", Res.string.error_no_book)
                },
            )
        }
    }
    //endregion
}