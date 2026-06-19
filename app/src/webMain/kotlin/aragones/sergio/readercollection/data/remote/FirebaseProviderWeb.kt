/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package aragones.sergio.readercollection.data.remote

import aragones.sergio.readercollection.data.remote.model.BookResponse
import aragones.sergio.readercollection.data.remote.model.RequestStatus
import aragones.sergio.readercollection.data.remote.model.UserResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.Promise
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

class FirebaseProviderWeb : FirebaseProvider {

    companion object {
        private const val PUBLIC_PROFILES_PATH = "public_profiles"
        private const val USERS_PATH = "users"
        private const val FRIENDS_PATH = "friends"
        private const val EMAIL_KEY = "email"
        private const val UUID_KEY = "uuid"
    }

    private val json = Json { ignoreUnknownKeys = true }

    override fun getUser(): UserResponse? = getCurrentUserJs()?.let {
        UserResponse(
            id = it.uid,
            username = it.email ?: "",
        )
    }

    override suspend fun signIn(email: String, password: String) {
        signInEmailJs(getAuthJs(), email, password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        createUserJs(getAuthJs(), email, password).await()
    }

    override suspend fun updatePassword(password: String) {
        val user = getCurrentUserJs() ?: throw Exception("No authenticated user")
        updatePasswordJs(user, password).await()
    }

    override fun signOut() {
        signOutJs(getAuthJs())
    }

    override suspend fun deleteUser() {
        val user = getCurrentUserJs() ?: throw Exception("No authenticated user")
        deleteUserJs(user).await()
    }

    override suspend fun registerPublicProfile(username: String, userId: String) {
        val data = mapOf(UUID_KEY to userId, EMAIL_KEY to username)
        setDocumentJs(
            getFirestoreJs(),
            PUBLIC_PROFILES_PATH,
            userId,
            json.encodeToString(data),
        ).await()
    }

    override suspend fun isPublicProfileActive(username: String): Boolean {
        val result = queryDocumentsJs(
            getFirestoreJs(),
            PUBLIC_PROFILES_PATH,
            EMAIL_KEY,
            username,
        ).await()
        val docs = json.decodeFromString<List<Map<String, String>>>(result.toString())
        return docs.any { it[EMAIL_KEY] != null }
    }

    override suspend fun deletePublicProfile(userId: String) {
        deleteDocumentJs(getFirestoreJs(), PUBLIC_PROFILES_PATH, userId).await()
    }

    override suspend fun getUserFromDatabase(username: String, userId: String): UserResponse? {
        val result = queryDocumentsJs(
            getFirestoreJs(),
            PUBLIC_PROFILES_PATH,
            EMAIL_KEY,
            username,
        ).await()
        val docs = json.decodeFromString<List<Map<String, String>>>(result.toString())
        return docs.firstOrNull()?.let {
            val uuid = it[UUID_KEY]
            val email = it[EMAIL_KEY]
            if (uuid != null && email != null && uuid != userId) {
                UserResponse(
                    id = uuid,
                    username = email.split("@").first(),
                    status = RequestStatus.PENDING_FRIEND,
                )
            } else {
                null
            }
        }
    }

    override suspend fun getFriends(userId: String): List<UserResponse> {
        val collectionPath = "$USERS_PATH/$userId/$FRIENDS_PATH"
        val result = getDocumentsJs(getFirestoreJs(), collectionPath).await()
        return json.decodeFromString<List<UserResponse>>(result.toString())
    }

    override suspend fun getFriend(userId: String, friendId: String): UserResponse? {
        val collectionPath = "$USERS_PATH/$userId/$FRIENDS_PATH"
        val result = getDocumentJs(
            getFirestoreJs(),
            collectionPath,
            friendId,
        ).await() ?: return null
        return json.decodeFromString<UserResponse>(result.toString())
    }

    override suspend fun requestFriendship(user: UserResponse, friend: UserResponse) {
        requestFriendshipJs(
            getFirestoreJs(),
            json.encodeToString(user),
            json.encodeToString(friend),
        ).await()
    }

    override suspend fun acceptFriendRequest(userId: String, friendId: String) {
        updateFriendStatusJs(
            getFirestoreJs(),
            userId,
            friendId,
            RequestStatus.APPROVED.name,
        ).await()
    }

    override suspend fun rejectFriendRequest(userId: String, friendId: String) {
        updateFriendStatusJs(
            getFirestoreJs(),
            userId,
            friendId,
            RequestStatus.REJECTED.name,
        ).await()
    }

    override suspend fun deleteFriendship(userId: String, friendId: String) {
        deleteFriendshipJs(getFirestoreJs(), userId, friendId).await()
    }

    override suspend fun deleteFriends(userId: String) {
        deleteFriendsJs(getFirestoreJs(), userId).await()
    }

    override suspend fun deleteUserFromDatabase(userId: String) {
        deleteDocumentJs(getFirestoreJs(), USERS_PATH, userId).await()
    }

    override fun getBooks(userId: String): Flow<List<Pair<String, Map<String, Any?>>>> =
        callbackFlow {
            val unsubscribe = onBooksSnapshotJs(getFirestoreJs(), userId) { result ->
                try {
                    val books = json.decodeFromString<List<JsonObject>>(result.toString())
                    val mappedBooks = books.map { bookObj ->
                        val id = bookObj["id"]?.jsonPrimitive?.content ?: ""
                        id to (bookObj.toAny() as Map<String, Any?>)
                    }
                    trySend(mappedBooks)
                } catch (e: Exception) {
                    println("Error decoding books: ${e.message}")
                }
                null
            }
            awaitClose {
                callJsFunction(unsubscribe)
            }
        }

    override suspend fun getBook(userId: String, bookId: String): Map<String, Any?> {
        val result = getBookJs(getFirestoreJs(), userId, bookId).await() ?: return emptyMap()
        return try {
            val book = json.decodeFromString<JsonObject>(result.toString())
            book.toAny() as Map<String, Any?>
        } catch (e: Exception) {
            println("Error decoding book $bookId: ${e.message}")
            emptyMap()
        }
    }

    override suspend fun syncBooks(
        uuid: String,
        booksToSave: List<BookResponse>,
        booksToRemove: List<BookResponse>,
    ) {
        syncBooksJs(
            getFirestoreJs(),
            uuid,
            json.encodeToString(booksToSave),
            json.encodeToString(booksToRemove),
        ).await()
    }

    override suspend fun deleteBooks(userId: String) {
        deleteBooksJs(getFirestoreJs(), userId).await()
    }

    override fun fetchRemoteConfigString(key: String, onCompletion: (String) -> Unit) {
        val config = getRemoteConfigJs()
        onCompletion(getRemoteConfigStringJs(config, key))
        fetchAndActivateJs(config).then {
            onCompletion(getRemoteConfigStringJs(config, key))
            null
        }
    }

    override suspend fun getRemoteConfigString(key: String): String {
        val config = getRemoteConfigJs()
        try {
            fetchAndActivateJs(config).await()
        } catch (_: Exception) {
        }
        return getRemoteConfigStringJs(config, key)
    }

    private fun JsonElement.toAny(): Any? = when (this) {
        is JsonPrimitive -> {
            if (isString) {
                content
            } else {
                val c = content
                if (c == "true" || c == "false") {
                    c.toBoolean()
                } else {
                    c.toDoubleOrNull() ?: c.toIntOrNull() ?: c
                }
            }
        }
        is JsonObject -> {
            mapValues { it.value.toAny() }
        }
        is JsonArray -> {
            map { it.toAny() }
        }
        is JsonNull -> {
            null
        }
    }
}

// Definiciones de tipos para WasmJS
external interface JsAuthUser : JsAny {
    val uid: String
    val email: String?
}

// Interoperabilidad directa
@JsFun("() => window.firebaseAuth")
external fun getAuthJs(): JsAny

@JsFun("() => window.firebaseAuth.currentUser")
external fun getCurrentUserJs(): JsAuthUser?

@JsFun(
    "(auth, email, pass) => window.firebaseAuthModule.createUserWithEmailAndPassword(auth, email, pass)",
)
external fun createUserJs(auth: JsAny, email: String, pass: String): Promise<JsAny>

@JsFun(
    "(auth, email, pass) => window.firebaseAuthModule.signInWithEmailAndPassword(auth, email, pass)",
)
external fun signInEmailJs(auth: JsAny, email: String, pass: String): Promise<JsAny>

@JsFun("(user, newPass) => window.firebaseAuthModule.updatePassword(user, newPass)")
external fun updatePasswordJs(user: JsAuthUser, newPass: String): Promise<JsAny?>

@JsFun("(auth) => window.firebaseAuthModule.signOut(auth)")
external fun signOutJs(auth: JsAny): Promise<JsAny?>

@JsFun("(user) => window.firebaseAuthModule.deleteUser(user)")
external fun deleteUserJs(user: JsAuthUser): Promise<JsAny?>

@JsFun("() => window.firebaseFirestore")
external fun getFirestoreJs(): JsAny

@JsFun(
    "(db, userId, bookId, json) => window.firebaseFirestoreModule.saveBook(db, userId, bookId, json)",
)
external fun saveBookJs(db: JsAny, userId: String, bookId: String, json: String): Promise<JsAny?>

@JsFun("(db, userId, bookId) => window.firebaseFirestoreModule.getBook(db, userId, bookId)")
external fun getBookJs(db: JsAny, userId: String, bookId: String): Promise<JsString?>

@JsFun("(db, userId) => window.firebaseFirestoreModule.getBooks(db, userId)")
external fun getBooksJs(db: JsAny, userId: String): Promise<JsString>

@JsFun(
    "(db, userId, callback) => window.firebaseFirestoreModule.onBooksSnapshot(db, userId, callback)",
)
external fun onBooksSnapshotJs(db: JsAny, userId: String, callback: (JsString) -> JsAny?): JsAny

@JsFun("(f) => f()")
external fun callJsFunction(f: JsAny)

@JsFun("(db, coll, doc, json) => window.firebaseFirestoreModule.setDocument(db, coll, doc, json)")
external fun setDocumentJs(db: JsAny, coll: String, doc: String, json: String): Promise<JsAny?>

@JsFun("(db, coll, doc) => window.firebaseFirestoreModule.getDocument(db, coll, doc)")
external fun getDocumentJs(db: JsAny, coll: String, doc: String): Promise<JsString?>

@JsFun("(db, coll, doc) => window.firebaseFirestoreModule.deleteDocument(db, coll, doc)")
external fun deleteDocumentJs(db: JsAny, coll: String, doc: String): Promise<JsAny?>

@JsFun("(db, coll) => window.firebaseFirestoreModule.getDocuments(db, coll)")
external fun getDocumentsJs(db: JsAny, coll: String): Promise<JsString>

@JsFun(
    "(db, coll, field, value) => window.firebaseFirestoreModule.queryDocuments(db, coll, field, value)",
)
external fun queryDocumentsJs(
    db: JsAny,
    coll: String,
    field: String,
    value: String,
): Promise<JsString>

@JsFun(
    "(db, userId, booksToSaveJson, booksToRemoveJson) => window.firebaseFirestoreModule.syncBooks(db, userId, booksToSaveJson, booksToRemoveJson)",
)
external fun syncBooksJs(
    db: JsAny,
    userId: String,
    booksToSaveJson: String,
    booksToRemoveJson: String,
): Promise<JsAny?>

@JsFun("(db, userId) => window.firebaseFirestoreModule.deleteBooks(db, userId)")
external fun deleteBooksJs(db: JsAny, userId: String): Promise<JsAny?>

@JsFun(
    "(db, userJson, friendJson) => window.firebaseFirestoreModule.requestFriendship(db, userJson, friendJson)",
)
external fun requestFriendshipJs(db: JsAny, userJson: String, friendJson: String): Promise<JsAny?>

@JsFun(
    "(db, userId, friendId, status) => window.firebaseFirestoreModule.updateFriendStatus(db, userId, friendId, status)",
)
external fun updateFriendStatusJs(
    db: JsAny,
    userId: String,
    friendId: String,
    status: String,
): Promise<JsAny?>

@JsFun(
    "(db, userId, friendId) => window.firebaseFirestoreModule.deleteFriendship(db, userId, friendId)",
)
external fun deleteFriendshipJs(db: JsAny, userId: String, friendId: String): Promise<JsAny?>

@JsFun("(db, userId) => window.firebaseFirestoreModule.deleteFriends(db, userId)")
external fun deleteFriendsJs(db: JsAny, userId: String): Promise<JsAny?>

@JsFun("() => window.firebaseRemoteConfig")
external fun getRemoteConfigJs(): JsAny

@JsFun("(config) => window.firebaseRemoteConfigModule.fetchAndActivate(config)")
external fun fetchAndActivateJs(config: JsAny): Promise<JsAny?>

@JsFun("(config, key) => window.firebaseRemoteConfigModule.getString(config, key)")
external fun getRemoteConfigStringJs(config: JsAny, key: String): String

suspend fun <T : JsAny?> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    this.then(
        { result ->
            cont.resume(result)
            null // En WasmJS las lambdas de JS interop deben retornar JsAny?
        },
        { error ->
            cont.resumeWithException(RuntimeException(error.toString()))
            null
        },
    )
}
