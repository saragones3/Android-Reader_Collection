//
//  FirebaseProviderIos.swift
//  ReaderCollection
//
//  Created by Sergio Aragonés on 22/1/26.
//

import App
import FirebaseAuth
import FirebaseFirestore
import FirebaseRemoteConfig

private let PUBLIC_PROFILES_PATH = "public_profiles"
private let USERS_PATH = "users"
private let BOOKS_PATH = "books"
private let FRIENDS_PATH = "friends"
private let EMAIL_KEY = "email"
private let UUID_KEY = "uuid"

class FirebaseProviderIos: FirebaseProvider {
    
    static let instance = FirebaseProviderIos()
    
    private let auth: Auth
    private let firestore: Firestore
    private let remoteConfig: RemoteConfig
    
    private init() {
        auth = Auth.auth()
        
        firestore = Firestore.firestore()
        
        remoteConfig = RemoteConfig.remoteConfig()
        let settings = RemoteConfigSettings()
        settings.minimumFetchInterval = 3600
        remoteConfig.configSettings = settings
    }
    
    func getUser() -> UserResponse? {
        guard let user = auth.currentUser else { return nil }
        return UserResponse(
            id: user.uid,
            username: user.displayName ?? "",
            email: user.email ?? "",
            status: RequestStatus.pendingFriend
        )
    }
    
    func signIn(email: String, password: String) async throws {
        try await auth.signIn(withEmail: email, password: password)
    }
    
    func signUp(email: String, password: String) async throws {
        do {
            try await auth.createUser(withEmail: email, password: password)
        } catch {
            throw error
        }
    }
    
    func updatePassword(password: String) async throws {
        guard let user = auth.currentUser else {
            fatalError("User is null")
        }
        try await user.updatePassword(to: password)
    }

    func updateEmail(email: String) async throws {
        guard let user = auth.currentUser else {
            fatalError("User is null")
        }
        try await user.updateEmail(to: email)
    }

    func updateDisplayName(displayName: String) async throws {
        guard let user = auth.currentUser else {
            fatalError("User is null")
        }
        let changeRequest = user.createProfileChangeRequest()
        changeRequest.displayName = displayName
        try await changeRequest.commitChanges()
    }
    
    func signOut() {
        do {
            try auth.signOut()
        } catch {}
    }
    
    func deleteUser() async throws {
        try await auth.currentUser?.delete()
    }
    
    func registerPublicProfile(username: String, userId: String) async throws {
        try await firestore
            .collection(PUBLIC_PROFILES_PATH)
            .document(userId)
            .setData([UUID_KEY : userId, EMAIL_KEY : username])
    }
    
    func isPublicProfileActive(username: String) async throws -> KotlinBoolean {
        let isActive = try await firestore
            .collection(PUBLIC_PROFILES_PATH)
            .whereField(EMAIL_KEY, isEqualTo: username)
            .getDocuments()
            .documents
            .first?
            .get(EMAIL_KEY) != nil
        return KotlinBoolean(bool: isActive)
    }
    
    func deletePublicProfile(userId: String) async throws {
        try await firestore
            .collection(PUBLIC_PROFILES_PATH)
            .document(userId)
            .delete()
    }
    
    func getPublicUsers(username: String, userId: String) async throws -> [UserResponse] {
        let result = try await firestore
            .collection(PUBLIC_PROFILES_PATH)
            .getDocuments()
            .documents
        
        return result.compactMap { document in
            guard let uuid = document.get(UUID_KEY) as? String,
                  let email = document.get(EMAIL_KEY) as? String,
                  uuid != userId,
                  email.localizedCaseInsensitiveContains(username) else {
                return nil
            }
            return UserResponse(
                id: uuid,
                username: String(email.split(separator: "@").first ?? ""),
                email: "",
                status: RequestStatus.pendingFriend,
            )
        }
    }
    
    func getFriends(userId: String) async throws -> [UserResponse] {
        return try await firestore
            .collection(USERS_PATH)
            .document(userId)
            .collection(FRIENDS_PATH)
            .getDocuments()
            .documents
            .map({ try $0.data(as: UserResponseIos.self).toUserResponse() })
    }
    
    func getFriend(userId: String, friendId: String) async throws -> UserResponse? {
        return try await firestore
            .collection(USERS_PATH)
            .document(userId)
            .collection(FRIENDS_PATH)
            .document(friendId)
            .getDocument()
            .data(as: UserResponseIos.self)
            .toUserResponse()
    }
    
    func requestFriendship(user: UserResponse, friend: UserResponse) async throws {
        let batch = firestore.batch()
        
        let userRef = firestore
            .collection(USERS_PATH)
            .document(user.id)
            .collection(FRIENDS_PATH)
            .document(friend.id)
        let userData: [String : Any] = [
            "id" : friend.id,
            "username" : friend.username,
            "status" : friend.status.name,
        ]
        batch.setData(userData, forDocument: userRef)
        
        let friendRef = firestore
            .collection(USERS_PATH)
            .document(friend.id)
            .collection(FRIENDS_PATH)
            .document(user.id)
        let friendData: [String : Any] = [
            "id" : user.id,
            "username" : user.username,
            "status" : user.status.name,
        ]
        batch.setData(friendData, forDocument: friendRef)
        
        try await batch.commit()
    }
    
    func acceptFriendRequest(userId: String, friendId: String) async throws {
        let userRef = firestore
            .collection(USERS_PATH)
            .document(userId)
            .collection(FRIENDS_PATH)
            .document(friendId)
        
        let friendRef = firestore
            .collection(USERS_PATH)
            .document(friendId)
            .collection(FRIENDS_PATH)
            .document(userId)
        
        let batch = firestore.batch()
        
        batch.updateData(["status" : RequestStatus.approved.name], forDocument: userRef)
        batch.updateData(["status" : RequestStatus.approved.name], forDocument: friendRef)
        
        try await batch.commit()
    }
    
    func rejectFriendRequest(userId: String, friendId: String) async throws {
        let userRef = firestore
            .collection(USERS_PATH)
            .document(userId)
            .collection(FRIENDS_PATH)
            .document(friendId)
        
        let friendRef = firestore
            .collection(USERS_PATH)
            .document(friendId)
            .collection(FRIENDS_PATH)
            .document(userId)
        
        let batch = firestore.batch()
        
        batch.deleteDocument(userRef)
        batch.updateData(["status" : RequestStatus.rejected.name], forDocument: friendRef)
        
        try await batch.commit()
    }
    
    func deleteFriendship(userId: String, friendId: String) async throws {
        let userRef = firestore
            .collection(USERS_PATH)
            .document(userId)
            .collection(FRIENDS_PATH)
            .document(friendId)
        
        let friendRef = firestore
            .collection(USERS_PATH)
            .document(friendId)
            .collection(FRIENDS_PATH)
            .document(userId)
        
        let batch = firestore.batch()
        
        batch.deleteDocument(userRef)
        batch.deleteDocument(friendRef)
        
        try await batch.commit()
    }
    
    func deleteFriends(userId: String) async throws {
        let batch = firestore.batch()
        let usersRef = firestore
            .collection(USERS_PATH)
        let friends = try await usersRef
            .document(userId)
            .collection(FRIENDS_PATH)
            .getDocuments()
            .documents
        friends.forEach({
            let friendRef = usersRef
                .document($0.documentID)
                .collection(FRIENDS_PATH)
                .document(userId)
            batch.deleteDocument(friendRef)
            batch.deleteDocument($0.reference)
        })
        try await batch.commit()
    }
    
    func deleteUserFromDatabase(userId: String) async throws {
        try await firestore
            .collection(USERS_PATH)
            .document(userId)
            .delete()
    }
    
    func getBooks(userId: String) -> Kotlinx_coroutines_coreFlow {
        return FlowUtilsKt.createBooksFlow(fetcher: { [weak self] callback in
            Task {
                do {
                    let books = try await self?.firestore
                        .collection(USERS_PATH)
                        .document(userId).collection(BOOKS_PATH)
                        .getDocuments()
                        .documents
                        .map({
                            KotlinPair(first: $0.documentID as NSString, second: $0.toMap() as NSDictionary)
                        }) ?? []
                    callback(books)
                } catch {
                    callback([])
                }
            }
        })
    }
    
    func getBook(userId: String, bookId: String) async throws -> [String : Any] {
        return try await firestore
            .collection(USERS_PATH)
            .document(userId)
            .collection(BOOKS_PATH)
            .document(bookId)
            .getDocument()
            .toMap()
    }
    
    func syncBooks(uuid: String, booksToSave: [BookResponse], booksToRemove: [BookResponse]) async throws {
        let batch = firestore.batch()
        let userRef = firestore
            .collection(USERS_PATH)
            .document(uuid)
        let booksRef = userRef
            .collection(BOOKS_PATH)
        
        booksToSave.forEach { book in
            let docRef = booksRef.document(book.id)
            var values = book.toMap()
            values["publishedDate"] = (values["publishedDate"] as? Date)?.toTimestamp()
            values["readingDate"] = (values["readingDate"] as? Date)?.toTimestamp()
            batch.setData(values, forDocument: docRef)
        }
        
        booksToRemove.forEach { book in
            let docRef = booksRef.document(book.id)
            batch.deleteDocument(docRef)
        }
        
        batch.setData(["lastUpdated" : FieldValue.serverTimestamp()], forDocument: userRef, merge: true)
        
        try await batch.commit()
    }
    
    
    func deleteBooks(userId: String) async throws {
        let batch = firestore.batch()
        let userRef = firestore
            .collection(USERS_PATH)
            .document(userId)
        let books = try await userRef
            .collection(BOOKS_PATH)
            .getDocuments()
            .documents
            .map({ $0.reference })
        books.forEach({
            batch.deleteDocument($0)
        })
        batch.setData(["lastUpdated" : FieldValue.serverTimestamp()], forDocument: userRef, merge: true)
        try await batch.commit()
    }
    
    func getLastUpdated(userId: String) async throws -> Any {
        let timestamp = try await firestore
            .collection(USERS_PATH)
            .document(userId)
            .getDocument()
            .get("lastUpdated") as? Timestamp
        return timestamp?.toDate() ?? NSNull()
    }
    
    func fetchRemoteConfigString(key: String, onCompletion: @escaping (String) -> Void) {
        onCompletion(remoteConfig.configValue(forKey: key).stringValue)
        
        remoteConfig.fetchAndActivate { [weak self] _, _ in
            guard let self = self else { return }
            onCompletion(self.remoteConfig.configValue(forKey: key).stringValue)
        }
    }
    
    func getRemoteConfigString(key: String) async throws -> String {
        try await remoteConfig.fetchAndActivate()
        return remoteConfig.configValue(forKey: key).stringValue
    }
}

extension DocumentSnapshot {
    func toMap() -> [String : Any] {
        return [
            "title" : get("title") as? String ?? NSNull(),
            "subtitle" : get("subtitle") as? String ?? NSNull(),
            "authors" : get("authors") ?? NSNull(),
            "publisher" : get("publisher") as? String ?? NSNull(),
            "publishedDate" : (get("publishedDate") as? Timestamp)?.toDate() ?? NSNull(),
            "readingDate" : (get("readingDate") as? Timestamp)?.toDate() ?? NSNull(),
            "description" : get("description") as? String ?? NSNull(),
            "summary" : get("summary") as? String ?? NSNull(),
            "isbn" : get("isbn") as? String ?? NSNull(),
            "pageCount" : get("pageCount") ?? NSNull(),
            "categories" : get("categories") ?? NSNull(),
            "averageRating" : get("averageRating") as? Double ?? NSNull(),
            "ratingsCount" : get("ratingsCount") ?? NSNull(),
            "rating" : get("rating") as? Double ?? NSNull(),
            "thumbnail" : get("thumbnail") as? String ?? NSNull(),
            "image" : get("image") as? String ?? NSNull(),
            "format" : get("format") as? String ?? NSNull(),
            "state" : get("state") as? String ?? NSNull(),
            "priority" : get("priority") ?? NSNull()
        ]
    }
}

extension Date {
    func toTimestamp() -> Timestamp {
        return Timestamp(date: self)
    }
}

extension Timestamp {
    func toDate() -> Date {
        return self.dateValue()
    }
}
