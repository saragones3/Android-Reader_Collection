package aragones.sergio.readercollection.presentation.<feature>

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDark
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme

@Composable
fun <Feature>Screen(
    state: <Feature>UiState,
    onBack: () -> Unit,
) {
    // UI Layout here
}

@CustomPreviewLightDark
@Composable
fun <Feature>ScreenPreview(
    @PreviewParameter(<Feature>ScreenPreviewParameterProvider::class) state: <Feature>UiState,
) {
    ReaderCollectionTheme {
        <Feature>Screen(
            state = state,
            onBack = {},
        )
    }
}

private class <Feature>ScreenPreviewParameterProvider : PreviewParameterProvider<<Feature>UiState> {
    override val values: Sequence<<Feature>UiState>
        get() = sequenceOf(
            <Feature>UiState(isLoading = false),
            <Feature>UiState(isLoading = true),
        )
}
