/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 18/10/2020
 */

package aragones.sergio.readercollection.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aragones.sergio.readercollection.domain.BooksRepository
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.Books
import aragones.sergio.readercollection.domain.model.ErrorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.error_search

class SearchViewModel(
    private val booksRepository: BooksRepository,
) : ViewModel() {

    //region Private properties
    private var query = ""
    private var page: Int = 1
    private val books = mutableListOf<Book>()
    val state: StateFlow<SearchUiState>
        field = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    //endregion

    //region Public methods
    fun searchBooks(reload: Boolean = false, query: String? = null) {
        if (reload) {
            page = 1
            books.clear()
        }

        if (query != null) {
            this.query = query
        }

        state.update {
            when (it) {
                SearchUiState.Empty -> SearchUiState.Success(
                    isLoading = true,
                    query = this.query,
                    books = Books(),
                    param = state.value.param,
                )
                is SearchUiState.Success -> it.copy(isLoading = true)
                is SearchUiState.Error -> it.copy(isLoading = true)
            }
        }

        viewModelScope.launch {
            booksRepository
                .searchBooks(
                    query = this@SearchViewModel.query,
                    filter = state.value.param.key,
                    page = page,
                    order = null,
                ).fold(
                    onSuccess = { newBooks ->
                        if (books.isEmpty()) {
                            books.add(Book(id = ""))
                        }
                        books.addAll(books.size - 1, newBooks)
                        if (newBooks.isEmpty()) {
                            books.removeAt(books.lastIndex)
                        }
                        val updatedBooks = mutableListOf<Book>().apply { addAll(books) }

                        page++
                        state.value = SearchUiState.Success(
                            isLoading = false,
                            query = this@SearchViewModel.query,
                            books = Books(updatedBooks),
                            param = state.value.param,
                        )
                    },
                    onFailure = {
                        state.value = SearchUiState.Error(
                            isLoading = false,
                            query = this@SearchViewModel.query,
                            value = ErrorModel("", Res.string.error_search),
                            param = state.value.param,
                        )
                    },
                )
        }
    }

    fun changeFilter(param: SearchParam) {
        state.update {
            when (it) {
                SearchUiState.Empty -> SearchUiState.Success(
                    isLoading = false,
                    query = this.query,
                    books = Books(),
                    param = param,
                )
                is SearchUiState.Success -> it.copy(param = param)
                is SearchUiState.Error -> it.copy(param = param)
            }
        }
    }
    //endregion
}