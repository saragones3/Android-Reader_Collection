/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 30/6/2025
 */

package aragones.sergio.readercollection.presentation.account

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.presentation.components.ButtonType
import aragones.sergio.readercollection.presentation.components.CustomCircularProgressIndicator
import aragones.sergio.readercollection.presentation.components.CustomOutlinedTextField
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDark
import aragones.sergio.readercollection.presentation.components.CustomToolbar
import aragones.sergio.readercollection.presentation.components.MainActionButton
import aragones.sergio.readercollection.presentation.components.withDescription
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import com.aragones.sergio.util.CustomInputType
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.account_title
import reader_collection.app.generated.resources.delete_account_action
import reader_collection.app.generated.resources.email
import reader_collection.app.generated.resources.hide_password
import reader_collection.app.generated.resources.invalid_email
import reader_collection.app.generated.resources.invalid_password
import reader_collection.app.generated.resources.password
import reader_collection.app.generated.resources.save
import reader_collection.app.generated.resources.show_info
import reader_collection.app.generated.resources.show_password
import reader_collection.app.generated.resources.username

@Composable
fun AccountScreen(
    state: AccountUiState,
    onShowInfo: () -> Unit,
    onProfileDataChange: (String, String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AccountToolbar(scrollState = scrollState, onBack = onBack)
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 12.dp)
                .verticalScroll(scrollState),
        ) {
            ProfileInfo(
                username = state.username,
                email = state.email,
                emailError = state.emailError,
                password = state.password,
                passwordError = state.passwordError,
                onShowInfo = onShowInfo,
                onEmailChange = {
                    onProfileDataChange(it, state.password)
                },
                onPasswordChange = {
                    onProfileDataChange(state.email, it)
                },
            )
            MainActionButton(
                text = stringResource(Res.string.save),
                onClick = onSave,
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 24.dp),
                enabled = state.emailError == null && state.passwordError == null,
            )
            Spacer(Modifier.weight(1f))
            MainActionButton(
                text = stringResource(Res.string.delete_account_action),
                onClick = onDeleteAccount,
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .align(Alignment.CenterHorizontally),
                type = ButtonType.DESTRUCTIVE,
            )
        }
    }
    if (state.isLoading) {
        CustomCircularProgressIndicator()
    }
}

@Composable
private fun AccountToolbar(scrollState: ScrollState, onBack: (() -> Unit)) {
    val elevation by remember {
        derivedStateOf {
            when (scrollState.value) {
                0 -> 0.dp
                else -> 4.dp
            }
        }
    }
    CustomToolbar(
        title = stringResource(Res.string.account_title),
        modifier = Modifier.shadow(elevation = elevation),
        backgroundColor = MaterialTheme.colorScheme.background,
        onBack = onBack,
    )
}

@Composable
private fun ProfileInfo(
    username: String,
    email: String,
    emailError: StringResource?,
    password: String,
    passwordError: StringResource?,
    onShowInfo: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    CustomOutlinedTextField(
        text = username,
        labelText = stringResource(Res.string.username),
        onTextChanged = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
    )
    Spacer(Modifier.height(8.dp))
    CustomOutlinedTextField(
        text = email,
        labelText = stringResource(Res.string.email),
        onTextChanged = onEmailChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        errorText = emailError?.let { stringResource(it) },
        endIcon = rememberVectorPainter(Icons.Default.Info)
            .withDescription(stringResource(Res.string.show_info))
            .takeIf { email.isBlank() },
        onEndIconClicked = onShowInfo,
    )
    Spacer(Modifier.height(8.dp))
    CustomOutlinedTextField(
        text = password,
        labelText = stringResource(Res.string.password),
        onTextChanged = onPasswordChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        errorText = passwordError?.let { stringResource(it) },
        endIcon = if (passwordVisibility) {
            rememberVectorPainter(Icons.Default.VisibilityOff)
                .withDescription(stringResource(Res.string.hide_password))
        } else {
            rememberVectorPainter(Icons.Default.Visibility)
                .withDescription(stringResource(Res.string.show_password))
        },
        inputType = CustomInputType.PASSWORD,
        isLastTextField = true,
        isRequired = true,
        onEndIconClicked = { passwordVisibility = !passwordVisibility },
    )
}

@CustomPreviewLightDark
@Composable
private fun AccountScreenPreview(
    @PreviewParameter(AccountScreenPreviewParameterProvider::class) state: AccountUiState,
) {
    ReaderCollectionTheme {
        AccountScreen(
            state = state,
            onShowInfo = {},
            onProfileDataChange = { _, _ -> },
            onBack = {},
            onSave = {},
            onDeleteAccount = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}

private class AccountScreenPreviewParameterProvider :
    PreviewParameterProvider<AccountUiState> {

    override val values: Sequence<AccountUiState>
        get() = sequenceOf(
            AccountUiState(
                username = "User",
                email = "user@example.com",
                password = "Password",
                emailError = null,
                passwordError = null,
                isLoading = false,
            ),
            AccountUiState(
                username = "Username very very very very very very very long",
                email = "user@example.com",
                emailError = Res.string.invalid_email,
                password = "",
                passwordError = Res.string.invalid_password,
                isLoading = false,
            ),
            AccountUiState(
                username = "User",
                email = "user@example.com",
                password = "Password",
                emailError = null,
                passwordError = null,
                isLoading = true,
            ),
        )
}