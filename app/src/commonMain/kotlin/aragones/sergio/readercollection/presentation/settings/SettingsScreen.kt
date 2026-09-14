/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 4/4/2024
 */

package aragones.sergio.readercollection.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.isAndroid
import aragones.sergio.readercollection.isiOS
import aragones.sergio.readercollection.presentation.components.ButtonType
import aragones.sergio.readercollection.presentation.components.CustomCard
import aragones.sergio.readercollection.presentation.components.CustomCircularProgressIndicator
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDarkLong
import aragones.sergio.readercollection.presentation.components.CustomToolbar
import aragones.sergio.readercollection.presentation.components.DropdownOutlinedTextField
import aragones.sergio.readercollection.presentation.components.DropdownValues
import aragones.sergio.readercollection.presentation.components.MainOutlinedButton
import aragones.sergio.readercollection.presentation.components.OptionData
import aragones.sergio.readercollection.presentation.components.OptionsSelector
import aragones.sergio.readercollection.presentation.components.SecondaryButton
import aragones.sergio.readercollection.presentation.components.withDescription
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import aragones.sergio.readercollection.presentation.theme.titleLargeEmphasized
import aragones.sergio.readercollection.presentation.theme.titleMediumEmphasized
import com.aragones.sergio.util.Preferences
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.account_description
import reader_collection.app.generated.resources.account_title
import reader_collection.app.generated.resources.app_language
import reader_collection.app.generated.resources.app_theme
import reader_collection.app.generated.resources.app_theme_values
import reader_collection.app.generated.resources.automatic_sync_description
import reader_collection.app.generated.resources.automatic_sync_title
import reader_collection.app.generated.resources.data_sync_title
import reader_collection.app.generated.resources.english
import reader_collection.app.generated.resources.last_synced
import reader_collection.app.generated.resources.logout_title
import reader_collection.app.generated.resources.profile_title
import reader_collection.app.generated.resources.public_profile_description
import reader_collection.app.generated.resources.public_profile_title
import reader_collection.app.generated.resources.settings_header_description
import reader_collection.app.generated.resources.settings_header_title
import reader_collection.app.generated.resources.sort_books_param
import reader_collection.app.generated.resources.sort_param
import reader_collection.app.generated.resources.sorting_order_values
import reader_collection.app.generated.resources.sorting_param_keys
import reader_collection.app.generated.resources.sorting_param_values
import reader_collection.app.generated.resources.spanish
import reader_collection.app.generated.resources.sync_now_action
import reader_collection.app.generated.resources.title_settings
import reader_collection.app.generated.resources.version
import reader_collection.app.generated.resources.visualization_title

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onClickOption: (SettingsOption) -> Unit,
    onChangePublicProfile: (Boolean) -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    onSync: () -> Unit,
    onDisplaySettingsChanged: (String, String?, Boolean, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SettingsToolbar(scrollState = scrollState)
        SettingsContent(
            state = state,
            scrollState = scrollState,
            onClickOption = onClickOption,
            onChangePublicProfile = onChangePublicProfile,
            onAutoSyncChange = onAutoSyncChange,
            onSync = onSync,
            onDisplaySettingsChanged = onDisplaySettingsChanged,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
    if (state.isLoading) {
        CustomCircularProgressIndicator()
    }
}

@Composable
private fun SettingsToolbar(scrollState: ScrollState) {
    val elevation by remember {
        derivedStateOf {
            when (scrollState.value) {
                0 -> 0.dp
                else -> 4.dp
            }
        }
    }
    CustomToolbar(
        title = stringResource(Res.string.title_settings),
        modifier = Modifier.shadow(elevation = elevation),
        backgroundColor = MaterialTheme.colorScheme.background,
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    scrollState: ScrollState,
    onClickOption: (SettingsOption) -> Unit,
    onChangePublicProfile: (Boolean) -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    onSync: () -> Unit,
    onDisplaySettingsChanged: (String, String?, Boolean, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Header(modifier = Modifier.padding(vertical = 16.dp))
        AccountSettingsCard(
            isProfilePublic = state.isProfilePublic,
            onChangePublicProfile = onChangePublicProfile,
            onClickOption = onClickOption,
        )
        if (isAndroid() || isiOS()) {
            SyncSettingsCard(
                isAutoSyncEnabled = state.isAutomaticSyncEnabled,
                lastSynced = state.lastSynced,
                onAutoSyncChange = onAutoSyncChange,
                onSync = onSync,
            )
        }
        DisplaySettingsCard(state, onDisplaySettingsChanged)
        Footer(state, onClickOption)
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_header_title),
            style = MaterialTheme.typography.titleLargeEmphasized,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(Res.string.settings_header_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun AccountSettingsCard(
    isProfilePublic: Boolean,
    onChangePublicProfile: (Boolean) -> Unit,
    onClickOption: (SettingsOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContainer(
        icon = Icons.Default.ManageAccounts,
        title = stringResource(Res.string.profile_title),
        modifier = modifier.fillMaxWidth(),
        content = {
            SettingItem(
                title = stringResource(Res.string.account_title),
                subtitle = stringResource(Res.string.account_description),
                onClick = {
                    onClickOption(SettingsOption.Account)
                },
            )
            PublicProfileItem(
                isEnabled = isProfilePublic,
                onChange = onChangePublicProfile,
            )
        },
    )
}

@Composable
private fun SyncSettingsCard(
    isAutoSyncEnabled: Boolean,
    lastSynced: String,
    onAutoSyncChange: (Boolean) -> Unit,
    onSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContainer(
        icon = Icons.Default.Sync,
        title = stringResource(Res.string.data_sync_title),
        modifier = modifier.fillMaxWidth(),
        content = {
            if (isAndroid()) {
                SyncAutomaticallyItem(
                    isEnabled = isAutoSyncEnabled,
                    onChange = onAutoSyncChange,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            SyncManuallyItem(
                lastSynced = lastSynced,
                onSync = onSync,
            )
        },
    )
}

@Composable
private fun DisplaySettingsCard(
    state: SettingsUiState,
    onDisplaySettingsChanged: (String, String?, Boolean, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContainer(
        icon = Icons.Default.Palette,
        title = stringResource(Res.string.visualization_title),
        modifier = modifier.fillMaxWidth(),
        content = {
            Spacer(modifier = Modifier.height(12.dp))
            if (isAndroid()) {
                AppThemeInfo(
                    selectedThemeIndex = state.themeMode,
                    onThemeChange = {
                        onDisplaySettingsChanged(
                            state.language,
                            state.sortParam,
                            state.isSortDescending,
                            it,
                        )
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
                LanguageInfo(
                    language = state.language,
                    onLanguageChange = {
                        onDisplaySettingsChanged(
                            it,
                            state.sortParam,
                            state.isSortDescending,
                            state.themeMode,
                        )
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            SortingInfo(
                sortParam = state.sortParam,
                isSortDescending = state.isSortDescending,
                onSortParamValueChange = {
                    onDisplaySettingsChanged(
                        state.language,
                        it,
                        state.isSortDescending,
                        state.themeMode,
                    )
                },
                onSortOrderValueChange = {
                    onDisplaySettingsChanged(
                        state.language,
                        state.sortParam,
                        it,
                        state.themeMode,
                    )
                },
            )
        },
    )
}

@Composable
private fun ColumnScope.Footer(state: SettingsUiState, onClickOption: (SettingsOption) -> Unit) {
    MainOutlinedButton(
        text = stringResource(Res.string.logout_title),
        onClick = {
            onClickOption(SettingsOption.Logout)
        },
        modifier = Modifier
            .padding(vertical = 24.dp)
            .align(Alignment.CenterHorizontally),
        type = ButtonType.DESTRUCTIVE,
        painter = rememberVectorPainter(
            Icons.AutoMirrored.Default.Logout,
        ).withDescription(null),
    )
    Spacer(Modifier.weight(1f))
    Text(
        text = stringResource(Res.string.version, state.version),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.tertiary,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun PublicProfileItem(
    isEnabled: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.public_profile_title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.public_profile_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isEnabled,
            onCheckedChange = onChange,
            thumbContent = {
                Icon(
                    imageVector = if (isEnabled) {
                        Icons.Default.Public
                    } else {
                        Icons.Default.PublicOff
                    },
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp),
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedIconColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                uncheckedIconColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
                uncheckedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            ),
        )
    }
}

@Composable
private fun SyncAutomaticallyItem(
    isEnabled: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.automatic_sync_title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.automatic_sync_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isEnabled,
            onCheckedChange = onChange,
            thumbContent = {
                Icon(
                    imageVector = if (isEnabled) {
                        Icons.Default.CloudDone
                    } else {
                        Icons.Default.CloudOff
                    },
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp),
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedIconColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                uncheckedIconColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
                uncheckedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            ),
        )
    }
}

@Composable
private fun SyncManuallyItem(
    lastSynced: String,
    onSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondary,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.last_synced, lastSynced),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
            SecondaryButton(
                text = stringResource(Res.string.sync_now_action),
                onClick = onSync,
                modifier = Modifier.fillMaxWidth(),
                startPainter = rememberVectorPainter(Icons.Default.Backup).withDescription(null),
            )
        }
    }
}

@Composable
private fun SectionContainer(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    CustomCard(
        modifier = modifier,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = title,
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
        },
    )
}

@Composable
private fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun AppThemeInfo(selectedThemeIndex: Int, onThemeChange: (Int) -> Unit) {
    val appThemes = stringArrayResource(Res.array.app_theme_values)
    val images = listOf(
        Icons.Default.Brightness6,
        Icons.Default.LightMode,
        Icons.Default.DarkMode,
    )
    val themeChange: (Int) -> Unit = {
        onThemeChange(it)
    }
    val displayOrder = listOf(0, 1, 2)
    val themeOptions = displayOrder.map { index ->
        OptionData(
            text = appThemes.getOrNull(index) ?: "",
            icon = rememberVectorPainter(images[index]).withDescription(null),
            isSelected = selectedThemeIndex == index,
            onClick = { themeChange(index) },
        )
    }
    Title(text = stringResource(Res.string.app_theme))
    OptionsSelector(options = themeOptions)
}

@Composable
private fun LanguageInfo(language: String, onLanguageChange: (String) -> Unit) {
    val values = listOf(
        stringResource(Res.string.english),
        stringResource(Res.string.spanish),
    )
    val keys = listOf(
        Preferences.ENGLISH_LANGUAGE_KEY,
        Preferences.SPANISH_LANGUAGE_KEY,
    )
    val languageChange: (String) -> Unit = {
        onLanguageChange(it)
    }
    val languageOptions = keys.mapIndexed { index, value ->
        OptionData(
            text = values[index],
            isSelected = language == value,
            onClick = { languageChange(value) },
        )
    }
    Title(text = stringResource(Res.string.app_language))
    OptionsSelector(options = languageOptions)
}

@Composable
private fun SortingInfo(
    sortParam: String?,
    isSortDescending: Boolean,
    onSortParamValueChange: (String?) -> Unit,
    onSortOrderValueChange: (Boolean) -> Unit,
) {
    Title(text = stringResource(Res.string.sort_books_param))

    val sortingParamValues = stringArrayResource(Res.array.sorting_param_values)
    val sortingParamKeys = stringArrayResource(Res.array.sorting_param_keys)
    val sortParamValue =
        if (sortParam == null) {
            sortingParamValues.firstOrNull() ?: ""
        } else {
            val index = sortingParamKeys.indexOf(sortParam)
            if (index != -1 && index < sortingParamValues.size) {
                sortingParamValues[index]
            } else {
                sortingParamValues.firstOrNull() ?: ""
            }
        }
    DropdownOutlinedTextField(
        currentValue = sortParamValue,
        values = DropdownValues(sortingParamValues),
        labelText = stringResource(Res.string.sort_param),
        onOptionSelected = {
            val index = sortingParamValues.indexOf(it)
            val newSortParam = sortingParamKeys.getOrNull(index)?.takeIf { index != 0 }
            onSortParamValueChange(newSortParam)
        },
    )

    Spacer(Modifier.height(12.dp))

    val sortingOrderKeys = listOf(false, true)
    val sortingOrderValues = stringArrayResource(Res.array.sorting_order_values)
    val images = listOf(
        Icons.Default.ArrowUpward,
        Icons.Default.ArrowDownward,
    )
    val sortOrderChange: (Boolean) -> Unit = {
        onSortOrderValueChange(it)
    }
    val sortingOrderOptions = sortingOrderKeys.mapIndexed { index, value ->
        OptionData(
            text = sortingOrderValues.getOrNull(index) ?: "",
            icon = rememberVectorPainter(images[index]).withDescription(null),
            isSelected = isSortDescending == value,
            onClick = { sortOrderChange(value) },
        )
    }
    OptionsSelector(options = sortingOrderOptions)
}

@Composable
fun SettingItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@CustomPreviewLightDarkLong
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsScreenPreviewParameterProvider::class) isLoading: Boolean,
) {
    ReaderCollectionTheme {
        SettingsScreen(
            state = SettingsUiState(
                isProfilePublic = false,
                isAutomaticSyncEnabled = true,
                lastSynced = "Today, 10:42AM",
                language = "en",
                sortParam = null,
                isSortDescending = false,
                themeMode = 0,
                version = "1.0.0",
                isLoading = isLoading,
            ),
            onClickOption = {},
            onChangePublicProfile = {},
            onAutoSyncChange = {},
            onSync = {},
            onDisplaySettingsChanged = { _, _, _, _ -> },
        )
    }
}

private class SettingsScreenPreviewParameterProvider :
    PreviewParameterProvider<Boolean> {

    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}