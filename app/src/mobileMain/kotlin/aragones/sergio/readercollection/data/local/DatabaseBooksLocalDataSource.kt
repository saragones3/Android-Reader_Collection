/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.local

import aragones.sergio.readercollection.data.remote.model.ALL_FORMATS
import aragones.sergio.readercollection.data.remote.model.ALL_GENRES
import aragones.sergio.readercollection.data.remote.model.ALL_STATES
import aragones.sergio.readercollection.domain.model.Book
import com.aragones.sergio.BooksLocalDataSource as RoomBooksLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseBooksLocalDataSource(
    private val booksLocalDataSource: RoomBooksLocalDataSource,
    private val preferences: SharedPreferencesHandler,
) : BooksLocalDataSource {

    //region Public methods
    override fun getAllBooks(): Flow<List<Book>> = booksLocalDataSource.getAllBooks().map { books ->
        books.map { it.toDomain() }
    }

    override fun getReadBooks(): Flow<List<Book>> =
        booksLocalDataSource.getReadBooks().map { books ->
            books.map { it.toDomain() }
        }

    override suspend fun importDataFrom(books: List<Book>) = booksLocalDataSource.importDataFrom(
        books.map {
            it.toLocalData()
        },
    )

    override suspend fun getBook(googleId: String): Book? =
        booksLocalDataSource.getBook(googleId)?.toDomain()

    override suspend fun insertBooks(books: List<Book>) = booksLocalDataSource.insertBooks(
        books.map {
            it.toLocalData()
        },
    )

    override suspend fun updateBooks(books: List<Book>) = booksLocalDataSource.updateBooks(
        books.map {
            it.toLocalData()
        },
    )

    override suspend fun deleteBooks(books: List<Book>) = booksLocalDataSource.deleteBooks(
        books.map {
            it.toLocalData()
        },
    )

    override fun saveRemoteConfigValues() {
        preferences.formats = ALL_FORMATS
        preferences.genres = ALL_GENRES
        preferences.states = ALL_STATES
    }

    override fun retrieveRemoteConfigValues() {
        ALL_FORMATS = preferences.formats
        ALL_GENRES = preferences.genres
        ALL_STATES = preferences.states
    }
    //endregion
}