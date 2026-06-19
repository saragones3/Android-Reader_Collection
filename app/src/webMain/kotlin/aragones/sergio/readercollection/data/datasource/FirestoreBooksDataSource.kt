/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.datasource

import aragones.sergio.readercollection.data.local.BooksLocalDataSource
import aragones.sergio.readercollection.data.local.SharedPreferencesHandler
import aragones.sergio.readercollection.domain.model.Book
import kotlinx.coroutines.flow.Flow

class FirestoreBooksDataSource(
    private val preferences: SharedPreferencesHandler,
) : BooksLocalDataSource {

    override fun getAllBooks(): Flow<List<Book>> {
        TODO()
    }

    override fun getReadBooks(): Flow<List<Book>> {
        TODO()
    }

    override suspend fun importDataFrom(books: List<Book>) {
        TODO()
    }

    override suspend fun getBook(googleId: String): Book? {
        TODO()
    }

    override suspend fun insertBooks(books: List<Book>) {
        TODO()
    }

    override suspend fun updateBooks(books: List<Book>) {
        TODO()
    }

    override suspend fun deleteBooks(books: List<Book>) {
        TODO()
    }
}