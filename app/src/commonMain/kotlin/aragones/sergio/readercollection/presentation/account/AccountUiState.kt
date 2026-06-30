/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 1/7/2025
 */

package aragones.sergio.readercollection.presentation.account

import org.jetbrains.compose.resources.StringResource

data class AccountUiState(
    val username: String,
    val email: String,
    val password: String,
    val emailError: StringResource?,
    val passwordError: StringResource?,
    val isProfilePublic: Boolean,
    val isLoading: Boolean,
) {
    companion object {
        fun empty(): AccountUiState = AccountUiState(
            username = "",
            email = "",
            password = "",
            emailError = null,
            passwordError = null,
            isProfilePublic = false,
            isLoading = false,
        )
    }
}
