/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 25/6/2026
 */

package aragones.sergio.readercollection.data.remote

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun createBooksFlow(
    fetcher: (callback: (List<Pair<String, Map<String, Any?>>>) -> Unit) -> Unit,
): Flow<List<Pair<String, Map<String, Any?>>>> = callbackFlow {
    fetcher { books ->
        trySend(books)
        close()
    }
    awaitClose { }
}
