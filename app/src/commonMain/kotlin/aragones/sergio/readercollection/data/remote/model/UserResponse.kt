/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 13/7/2025
 */

package aragones.sergio.readercollection.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    override val id: String = "",
    val username: String = "",
    val email: String = "",
    val status: RequestStatus = RequestStatus.PENDING_FRIEND,
) : BaseModel<String>

@Serializable
enum class RequestStatus {
    PENDING_MINE,
    PENDING_FRIEND,
    APPROVED,
    REJECTED,
}
