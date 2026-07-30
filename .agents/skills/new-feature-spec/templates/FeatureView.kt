package aragones.sergio.readercollection.presentation.<feature>

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import aragones.sergio.readercollection.presentation.components.InformationAlertDialog
import aragones.sergio.readercollection.presentation.components.LaunchedEffectOnce
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionApp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun <Feature>View(onBack: () -> Unit, viewModel: <Feature>ViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val infoDialogMessageId by viewModel.infoDialogMessageId.collectAsState()
    val error by viewModel.error.collectAsState()

    ReaderCollectionApp {
        <Feature>Screen(
            state = state,
            onBack = onBack,
        )
    }

    val infoText = infoDialogMessageId?.let { stringResource(it) } ?: ""
    InformationAlertDialog(show = infoText.isNotEmpty(), text = infoText) {
        viewModel.closeDialogs()
    }
    
    // Handle error dialog similarly...

    LaunchedEffectOnce {
        viewModel.onCreate()
    }
}
