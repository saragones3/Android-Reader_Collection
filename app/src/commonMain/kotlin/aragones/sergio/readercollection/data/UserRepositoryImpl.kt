/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 6/11/2020
 */

package aragones.sergio.readercollection.data

import aragones.sergio.readercollection.data.local.UserLocalDataSource
import aragones.sergio.readercollection.data.local.model.AuthData
import aragones.sergio.readercollection.data.local.model.UserData
import aragones.sergio.readercollection.data.remote.UserRemoteDataSource
import aragones.sergio.readercollection.data.remote.model.RequestStatus
import aragones.sergio.readercollection.data.remote.model.UserResponse
import aragones.sergio.readercollection.domain.UserRepository
import aragones.sergio.readercollection.domain.model.User
import aragones.sergio.readercollection.domain.toDomain
import aragones.sergio.readercollection.domain.toRemoteData
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class UserRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    //region Public properties
    override val usernameOrEmail: String
        get() = userLocalDataSource.userData.email.ifBlank { userLocalDataSource.username }

    override val username: String
        get() = userLocalDataSource.username

    override val userData: UserData
        get() = userLocalDataSource.userData

    override val userId: String
        get() = userLocalDataSource.userId

    override val isProfilePublic: Boolean
        get() = userLocalDataSource.isProfilePublic

    override val isAutomaticSyncEnabled: Boolean
        get() = userLocalDataSource.isAutomaticSyncEnabled

    override var language: String
        get() = userLocalDataSource.language
        set(value) {
            userLocalDataSource.language = value
        }

    override val isLoggedIn: Boolean
        get() = userLocalDataSource.isLoggedIn && userRemoteDataSource.userExists

    override val sortParam: String?
        get() = userLocalDataSource.sortParam

    override val isSortDescending: Boolean
        get() = userLocalDataSource.isSortDescending

    override val themeMode: Int
        get() = userLocalDataSource.themeMode
    //endregion

    //region Public methods
    override suspend fun login(username: String, password: String): Result<Unit> =
        withContext(ioDispatcher) {
            userRemoteDataSource.login(username, password).fold(
                onSuccess = { userResponse ->
                    val userData = UserData(
                        username = userResponse.username,
                        email = userResponse.email,
                        password = password,
                    )
                    val authData = AuthData(userResponse.id)
                    userLocalDataSource.storeLoginData(userData, authData)
                    Result.success(Unit)
                },
                onFailure = {
                    Result.failure(it)
                },
            )
        }

    override fun logout() {
        userRemoteDataSource.logout()
        userLocalDataSource.logout()
    }

    override suspend fun register(username: String, password: String): Result<Unit> =
        withContext(ioDispatcher) {
            userRemoteDataSource.register(username, password).fold(
                onSuccess = {
                    Result.success(Unit)
                },
                onFailure = {
                    Result.failure(it)
                },
            )
        }

    override suspend fun updatePassword(password: String): Result<Unit> =
        withContext(ioDispatcher) {
            val userData = userLocalDataSource.userData
            userRemoteDataSource.login(usernameOrEmail, userData.password).fold(
                onSuccess = {
                    userRemoteDataSource.updatePassword(password).fold(onSuccess = {
                        userLocalDataSource.storePassword(password)
                        val usernameOrEmail = userData.email.ifBlank { userData.username }
                        userRemoteDataSource.login(usernameOrEmail, password).fold(
                            onSuccess = { userResponse ->
                                userLocalDataSource.storeCredentials(AuthData(userResponse.id))
                                Result.success(Unit)
                            },
                            onFailure = {
                                Result.failure(it)
                            },
                        )
                    }, onFailure = {
                        Result.failure(it)
                    })
                },
                onFailure = {
                    Result.failure(it)
                },
            )
        }

    override suspend fun updateEmail(email: String): Result<Unit> = withContext(ioDispatcher) {
        val userData = userLocalDataSource.userData
        userRemoteDataSource.login(usernameOrEmail, userData.password).fold(
            onSuccess = {
                userRemoteDataSource.updateEmail(email).fold(onSuccess = {
                    val currentData = userLocalDataSource.userData
                    currentData.email = email
                    userLocalDataSource.storeLoginData(currentData, AuthData(userId))
                    Result.success(Unit)
                }, onFailure = {
                    Result.failure(it)
                })
            },
            onFailure = {
                Result.failure(it)
            },
        )
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> =
        withContext(ioDispatcher) {
            userRemoteDataSource.updateDisplayName(displayName)
        }

    override suspend fun setPublicProfile(value: Boolean): Result<Unit> =
        withContext(ioDispatcher) {
            withTimeout(TIMEOUT) {
                if (value) {
                    userRemoteDataSource.registerPublicProfile(username, userId)
                } else {
                    userRemoteDataSource.deletePublicProfile(userId)
                }
            }.fold(
                onSuccess = {
                    userLocalDataSource.storePublicProfile(value)
                    Result.success(Unit)
                },
                onFailure = {
                    Result.failure(it)
                },
            )
        }

    override suspend fun loadConfig() = withContext(ioDispatcher) {
        val isActive = withTimeout(TIMEOUT) {
            userRemoteDataSource.isPublicProfileActive(username)
        }.fold(
            onSuccess = { it },
            onFailure = { false },
        )
        userLocalDataSource.storePublicProfile(isActive)
    }

    override suspend fun getUsersWith(username: String): Result<List<User>> =
        withContext(ioDispatcher) {
            withTimeout(TIMEOUT) {
                userRemoteDataSource.getUsers(username, userId)
            }.fold(
                onSuccess = { users ->
                    val friends = withTimeout(TIMEOUT) {
                        userRemoteDataSource.getFriends(userId)
                    }.fold(
                        onSuccess = { it },
                        onFailure = { emptyList() },
                    )
                    val result = users
                        .map { user ->
                            friends.firstOrNull { it.id == user.id } ?: user
                        }.map { it.toDomain() }
                    Result.success(result)
                },
                onFailure = {
                    Result.failure(it)
                },
            )
        }

    override suspend fun getFriends(): List<User> = withContext(ioDispatcher) {
        withTimeout(TIMEOUT) {
            userRemoteDataSource.getFriends(userId)
        }.fold(
            onSuccess = { friends ->
                friends.map { it.toDomain() }
            },
            onFailure = {
                emptyList()
            },
        )
    }

    override suspend fun getFriend(friendId: String): Result<User> = withContext(ioDispatcher) {
        withTimeout(TIMEOUT) {
            userRemoteDataSource.getFriend(userId, friendId)
        }.fold(
            onSuccess = { friend ->
                Result.success(friend.toDomain())
            },
            onFailure = {
                Result.failure(it)
            },
        )
    }

    override suspend fun requestFriendship(friend: User): Result<Unit> = withContext(ioDispatcher) {
        val user = UserResponse(
            id = userId,
            username = username,
            status = RequestStatus.PENDING_MINE,
        )
        val remoteFriend = friend.toRemoteData()
        withTimeout(TIMEOUT) {
            userRemoteDataSource.requestFriendship(user, remoteFriend)
        }
    }

    override suspend fun acceptFriendRequest(friendId: String): Result<Unit> =
        withContext(ioDispatcher) {
            withTimeout(TIMEOUT) {
                userRemoteDataSource.acceptFriendRequest(userId, friendId)
            }
        }

    override suspend fun rejectFriendRequest(friendId: String): Result<Unit> =
        withContext(ioDispatcher) {
            withTimeout(TIMEOUT) {
                userRemoteDataSource.rejectFriendRequest(userId, friendId)
            }
        }

    override suspend fun deleteFriend(friendId: String): Result<Unit> = withContext(ioDispatcher) {
        withTimeout(TIMEOUT) {
            userRemoteDataSource.deleteFriend(userId, friendId)
        }
    }

    override suspend fun deleteUser(): Result<Unit> = withContext(ioDispatcher) {
        withTimeout(TIMEOUT) {
            userRemoteDataSource.login(usernameOrEmail, userData.password).fold(
                onSuccess = {
                    userRemoteDataSource.deleteUser(userId).fold(
                        onSuccess = {
                            userLocalDataSource.logout()
                            userLocalDataSource.removeUserData()
                            Result.success(it)
                        },
                        onFailure = {
                            Result.failure(it)
                        },
                    )
                },
                onFailure = {
                    Result.failure(it)
                },
            )
        }
    }

    override fun storeAutomaticSync(value: Boolean) {
        userLocalDataSource.storeAutomaticSync(value)
    }

    override fun storeLanguage(language: String) {
        userLocalDataSource.storeLanguage(language)
    }

    override fun storeSortParam(sortParam: String?) {
        userLocalDataSource.storeSortParam(sortParam)
    }

    override fun storeIsSortDescending(isSortDescending: Boolean) {
        userLocalDataSource.storeIsSortDescending(isSortDescending)
    }

    override fun storeThemeMode(themeMode: Int) {
        userLocalDataSource.storeThemeMode(themeMode)
    }

    override suspend fun isThereMandatoryUpdate(): Boolean {
        val currentVersion = userLocalDataSource.getCalculatedCurrentVersion()
        val minVersion = userRemoteDataSource.getCalculatedMinVersion()
        return currentVersion < minVersion
    }

    override fun applyTheme() {
        userLocalDataSource.applyTheme()
    }

    override fun getAppVersion(): String = userLocalDataSource.getCurrentVersion()
    //endregion
}

private val TIMEOUT = 10.seconds
