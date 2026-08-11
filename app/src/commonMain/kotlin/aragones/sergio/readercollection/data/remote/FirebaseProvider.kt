/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 8/1/2026
 */

package aragones.sergio.readercollection.data.remote

import aragones.sergio.readercollection.data.remote.model.BookResponse
import aragones.sergio.readercollection.data.remote.model.UserResponse
import kotlinx.coroutines.flow.Flow

interface FirebaseProvider {
    fun getUser(): UserResponse?
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun updatePassword(password: String)
    suspend fun updateEmail(email: String)
    suspend fun updateDisplayName(displayName: String)
    fun signOut()
    suspend fun deleteUser()
    suspend fun registerPublicProfile(username: String, userId: String)
    suspend fun isPublicProfileActive(username: String): Boolean
    suspend fun deletePublicProfile(userId: String)
    suspend fun getPublicUsers(username: String, userId: String): List<UserResponse>
    suspend fun getFriends(userId: String): List<UserResponse>
    suspend fun getFriend(userId: String, friendId: String): UserResponse?
    suspend fun requestFriendship(user: UserResponse, friend: UserResponse)
    suspend fun acceptFriendRequest(userId: String, friendId: String)
    suspend fun rejectFriendRequest(userId: String, friendId: String)
    suspend fun deleteFriendship(userId: String, friendId: String)
    suspend fun deleteFriends(userId: String)
    suspend fun deleteUserFromDatabase(userId: String)
    fun getBooks(userId: String): Flow<List<Pair<String, Map<String, Any?>>>>
    suspend fun getBook(userId: String, bookId: String): Map<String, Any?>
    suspend fun syncBooks(
        uuid: String,
        booksToSave: List<BookResponse>,
        booksToRemove: List<BookResponse>,
    )
    suspend fun deleteBooks(userId: String)
    suspend fun getLastUpdated(userId: String): Any?
    fun fetchRemoteConfigString(key: String, onCompletion: (String) -> Unit)
    suspend fun getRemoteConfigString(key: String): String
}