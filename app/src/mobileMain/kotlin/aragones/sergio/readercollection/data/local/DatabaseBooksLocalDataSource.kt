/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.local

import aragones.sergio.readercollection.domain.model.Book
import com.aragones.sergio.BooksLocalDataSource as RoomBooksLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseBooksLocalDataSource(
    private val booksLocalDataSource: RoomBooksLocalDataSource,
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
    //endregion
}