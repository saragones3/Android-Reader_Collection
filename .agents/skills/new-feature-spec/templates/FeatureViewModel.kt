package aragones.sergio.readercollection.presentation.<feature>

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import aragones.sergio.readercollection.domain.model.ErrorModel

class <Feature>ViewModel : ViewModel() {

    //region Public properties
    val state: StateFlow<<Feature>UiState>
        field = MutableStateFlow<<Feature>UiState>(
            // Add class initialization here
        )
    val error: StateFlow<ErrorModel?>
        field = MutableStateFlow<ErrorModel?>(null)
    val infoDialogMessageId: StateFlow<StringResource?>
        field = MutableStateFlow<StringResource?>(null)
    //endregion

    //region Lifecycle methods
    fun onCreate() {
        // Initialization logic
    }
    //endregion

    //region Public methods
    fun closeDialogs() {
        infoDialogMessageId.value = null
        error.value = null
    }
    //endregion

    //region Private methods
    // Helper logic
    //endregion
}
