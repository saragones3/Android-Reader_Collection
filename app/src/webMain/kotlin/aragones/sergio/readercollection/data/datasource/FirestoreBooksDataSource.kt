/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.datasource

import aragones.sergio.readercollection.data.local.BooksLocalDataSource
import aragones.sergio.readercollection.data.local.SharedPreferencesHandler
import aragones.sergio.readercollection.data.remote.FirebaseProvider
import aragones.sergio.readercollection.data.remote.toBook
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.toDomain
import aragones.sergio.readercollection.domain.toRemoteData
import com.aragones.sergio.util.BookState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FirestoreBooksDataSource(
    private val firebaseProvider: FirebaseProvider,
    private val preferences: SharedPreferencesHandler,
) : BooksLocalDataSource {

    override fun getAllBooks(): Flow<List<Book>> {
        val uuid = preferences.credentials.uuid
        return if (uuid.isNotEmpty()) {
            firebaseProvider.getBooks(uuid).map { books ->
                books.mapNotNull { it.second.toBook(it.first)?.toDomain() }
            }
        } else {
            flowOf(emptyList())
        }
    }

    override fun getReadBooks(): Flow<List<Book>> = getAllBooks().map { books ->
        books.filter { it.state == BookState.READ }
    }

    override suspend fun importDataFrom(books: List<Book>) {
        try {
            val uuid = preferences.credentials.uuid
            if (uuid.isNotEmpty()) {
                firebaseProvider.syncBooks(uuid, books.map { it.toRemoteData() }, emptyList())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getBook(googleId: String): Book? {
        val uuid = preferences.credentials.uuid
        return if (uuid.isNotEmpty()) {
            firebaseProvider.getBook(uuid, googleId).toBook(googleId)?.toDomain()
        } else {
            null
        }
    }

    override suspend fun insertBooks(books: List<Book>) {
        try {
            val uuid = preferences.credentials.uuid
            if (uuid.isNotEmpty()) {
                firebaseProvider.syncBooks(uuid, books.map { it.toRemoteData() }, emptyList())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateBooks(books: List<Book>) {
        try {
            val uuid = preferences.credentials.uuid
            if (uuid.isNotEmpty()) {
                firebaseProvider.syncBooks(uuid, books.map { it.toRemoteData() }, emptyList())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteBooks(books: List<Book>) {
        try {
            val uuid = preferences.credentials.uuid
            if (uuid.isNotEmpty()) {
                firebaseProvider.syncBooks(uuid, emptyList(), books.map { it.toRemoteData() })
            }
        } catch (e: Exception) {
            throw e
        }
    }
}