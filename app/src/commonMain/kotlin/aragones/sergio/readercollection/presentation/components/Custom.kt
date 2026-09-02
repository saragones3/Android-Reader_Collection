/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 16/5/2024
 */

package aragones.sergio.readercollection.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.Books
import aragones.sergio.readercollection.presentation.theme.LightRoseBud
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import aragones.sergio.readercollection.presentation.theme.RoseBud
import aragones.sergio.readercollection.presentation.theme.isLight
import com.aragones.sergio.util.Constants
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.book_rating_description
import reader_collection.app.generated.resources.clear_text
import reader_collection.app.generated.resources.connection_lost
import reader_collection.app.generated.resources.connection_lost_description
import reader_collection.app.generated.resources.empty_library_description
import reader_collection.app.generated.resources.library_ready_title
import reader_collection.app.generated.resources.no_matching_records
import reader_collection.app.generated.resources.no_matching_records_description
import reader_collection.app.generated.resources.pending
import reader_collection.app.generated.resources.retry_search
import reader_collection.app.generated.resources.search
import reader_collection.app.generated.resources.show_all
import reader_collection.app.generated.resources.show_more
import reader_collection.app.generated.resources.star_empty
import reader_collection.app.generated.resources.star_filled
import reader_collection.app.generated.resources.star_half_filled
import reader_collection.app.generated.resources.star_rate_description
import reader_collection.app.generated.resources.star_status_description

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    isSelectable: Boolean = false,
    starSize: Dp = 24.dp,
    starSpacing: Dp = 2.dp,
) {
    val contentDescription =
        stringResource(Res.string.book_rating_description, (rating * 10 / maxStars).toInt())
    Row(
        modifier = modifier
            .selectableGroup()
            .semantics {
                this.contentDescription = contentDescription
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 1..maxStars) {
            val (icon, tint) = when {
                i <= rating -> Pair(
                    Icons.Default.Star,
                    RoseBud,
                )
                i.toFloat() == rating + 0.5f -> Pair(
                    Icons.AutoMirrored.Filled.StarHalf,
                    RoseBud,
                )
                else -> Pair(
                    Icons.Default.StarBorder,
                    LightRoseBud,
                )
            }
            val stateText = stringResource(
                when {
                    i <= rating -> Res.string.star_filled
                    i.toFloat() == rating + 0.5f -> Res.string.star_half_filled
                    else -> Res.string.star_empty
                },
            )
            val statusDescription =
                stringResource(Res.string.star_status_description, stateText, i, maxStars)
            val starContentDescription =
                pluralStringResource(Res.plurals.star_rate_description, i, statusDescription, i)
            Box(
                modifier = Modifier
                    .then(
                        if (!isSelectable) {
                            Modifier.semantics { hideFromAccessibility() }
                        } else {
                            Modifier
                        },
                    ).size(starSize),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = rememberVectorPainter(icon),
                    contentDescription = starContentDescription.takeIf { isSelectable },
                    tint = tint,
                    modifier = Modifier.fillMaxSize(),
                )
                if (isSelectable) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    val selectedRating = i - 0.5f
                                    onRatingChanged(
                                        if (selectedRating == rating) 0f else selectedRating,
                                    )
                                },
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        val selectedRating = i.toFloat()
                                        onRatingChanged(
                                            if (selectedRating == rating) 0f else selectedRating,
                                        )
                                    },
                                ),
                        )
                    }
                }
            }
            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}

@Composable
fun SearchBar(
    text: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    showLeadingIcon: Boolean = false,
    inputHintText: String = stringResource(Res.string.search),
    inputHintTextColor: Color = MaterialTheme.colorScheme.tertiary,
    textColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    requestFocusByDefault: Boolean = true,
    searchOnClear: Boolean = false,
) {
    var textFieldValueState by remember(text) {
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = when {
                    text.isEmpty() -> TextRange.Zero
                    else -> TextRange(text.length, text.length)
                },
            ),
        )
    }

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (requestFocusByDefault) focusManager.moveFocus(FocusDirection.Down)
    }

    val placeholder: @Composable (() -> Unit) = {
        Text(
            text = inputHintText,
            color = inputHintTextColor,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
    val leadingIcon: @Composable (() -> Unit)? = if (showLeadingIcon) {
        {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Search),
                contentDescription = stringResource(Res.string.search),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    } else {
        null
    }
    val trailingIcon: @Composable (() -> Unit)? = if (textFieldValueState.text.isNotBlank()) {
        {
            TopAppBarIcon(
                accessibilityPainter = rememberVectorPainter(Icons.Default.Clear)
                    .withDescription(stringResource(Res.string.clear_text)),
                onClick = {
                    textFieldValueState = TextFieldValue("")
                    if (searchOnClear) {
                        onSearch("")
                    }
                },
            )
        }
    } else {
        null
    }

    OutlinedTextField(
        value = textFieldValueState,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
        ),
        textStyle = textStyle.copy(color = textColor),
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboard?.hide()
                focusManager.clearFocus()
                onSearch(textFieldValueState.text)
            },
        ),
        singleLine = true,
        onValueChange = {
            textFieldValueState = it
        },
    )
}

@Composable
fun CustomFilterChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    selectedIcon: AccessibilityPainter? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        modifier = modifier,
        leadingIcon = selectedIcon?.let {
            {
                Icon(
                    painter = it.painter,
                    contentDescription = it.contentDescription,
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.primary,
            iconColor = MaterialTheme.colorScheme.primary,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.secondary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.secondary,
            selectedTrailingIconColor = MaterialTheme.colorScheme.secondary,
        ),
        border = border?.takeIf { !selected },
    )
}

@Composable
fun CustomInputChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onEndIconClick: (() -> Unit)? = null,
    endIcon: AccessibilityPainter? = null,
) {
    val trailingIcon: @Composable (() -> Unit)? = endIcon?.let {
        {
            Icon(
                painter = endIcon.painter,
                contentDescription = endIcon.contentDescription,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp).clickable { onEndIconClick?.invoke() },
            )
        }
    }
    InputChip(
        selected = false,
        onClick = onClick ?: {},
        label = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        },
        modifier = modifier,
        trailingIcon = trailingIcon,
        shape = MaterialTheme.shapes.medium,
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.primary,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
fun CustomOutlinedInputChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onEndIconClick: (() -> Unit)? = null,
    endIcon: AccessibilityPainter? = null,
) {
    val trailingIcon: @Composable (() -> Unit)? = endIcon?.let {
        {
            Icon(
                painter = endIcon.painter,
                contentDescription = endIcon.contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp).clickable { onEndIconClick?.invoke() },
            )
        }
    }
    InputChip(
        selected = false,
        onClick = onClick ?: {},
        label = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        },
        modifier = modifier,
        trailingIcon = trailingIcon,
        shape = MaterialTheme.shapes.medium,
        colors = InputChipDefaults.inputChipColors(
            containerColor = Color.Transparent,
            selectedContainerColor = Color.Transparent,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = 0.1.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
        content = {
            Column(
                modifier = contentModifier.padding(16.dp),
                content = content,
            )
        },
    )
}

@Composable
fun EmptyStateCard(
    subtitle: String,
    modifier: Modifier = Modifier,
    action: StateCardData.Action? = null,
) {
    StateCard(
        data = StateCardData.Empty(
            title = stringResource(Res.string.library_ready_title),
            subtitle = AnnotatedString(subtitle),
            action = action,
        ),
        modifier = modifier,
    )
}

@Composable
fun NoResultsStateCard(query: String, modifier: Modifier = Modifier) {
    StateCard(
        data = StateCardData.NoResults(
            title = stringResource(Res.string.no_matching_records),
            subtitle = getBoldTextFor(
                text = stringResource(Res.string.no_matching_records_description, query),
                placeholder = "\"" + query + "\"",
            ),
        ),
        modifier = modifier,
    )
}

@Composable
fun ErrorStateCard(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    StateCard(
        data = StateCardData.Error(
            title = stringResource(Res.string.connection_lost),
            subtitle = AnnotatedString(stringResource(Res.string.connection_lost_description)),
            action = StateCardData.Action(
                title = stringResource(Res.string.retry_search),
                onClick = onRetry,
                icon = rememberVectorPainter(Icons.Default.Refresh).withDescription(null),
            ),
        ),
        modifier = modifier,
    )
}

@Composable
fun StateCard(data: StateCardData, modifier: Modifier = Modifier) {
    val isLight = MaterialTheme.colorScheme.isLight()
    val backgroundColor = when (data) {
        is StateCardData.Empty, is StateCardData.NoResults -> MaterialTheme.colorScheme.surface
        is StateCardData.Error -> MaterialTheme.colorScheme.error.run {
            if (isLight) {
                this.copy(alpha = 0.05f)
            } else {
                this.copy(alpha = 0.2f)
            }
        }
    }
    val borderColor = when (data) {
        is StateCardData.Empty, is StateCardData.NoResults -> MaterialTheme.colorScheme.primary
        is StateCardData.Error -> MaterialTheme.colorScheme.error
    }
    val iconColor = when (data) {
        is StateCardData.Empty, is StateCardData.NoResults -> MaterialTheme.colorScheme.tertiary
        is StateCardData.Error -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(48.dp),
            )
            Text(
                text = data.title,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = data.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
            )
            data.action?.let { action ->
                Spacer(Modifier.height(8.dp))
                SecondaryOutlinedButton(
                    text = action.title,
                    onClick = action.onClick,
                    painter = action.icon,
                )
            }
        }
    }
}

sealed class StateCardData {
    abstract val icon: ImageVector
    abstract val title: String
    abstract val subtitle: AnnotatedString
    abstract val action: Action?

    data class Empty(
        override val icon: ImageVector = Icons.AutoMirrored.Filled.MenuBook,
        override val title: String,
        override val subtitle: AnnotatedString,
        override val action: Action? = null,
    ) : StateCardData()

    data class NoResults(
        override val icon: ImageVector = Icons.Default.SearchOff,
        override val title: String,
        override val subtitle: AnnotatedString,
        override val action: Action? = null,
    ) : StateCardData()

    data class Error(
        override val icon: ImageVector = Icons.Default.WifiOff,
        override val title: String,
        override val subtitle: AnnotatedString,
        override val action: Action?,
    ) : StateCardData()

    data class Action(
        val title: String,
        val onClick: () -> Unit,
        val icon: AccessibilityPainter? = null,
    )
}

@Composable
internal fun BooksSection(
    title: String,
    books: Books,
    onShowAll: (() -> Unit)?,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSwitchingEnabled: Boolean = false,
    showDivider: Boolean = true,
    onSwitchToLeft: (Int) -> Unit = {},
    onSwitchToRight: (Int) -> Unit = {},
    onLongClickBook: (Book) -> Unit = {},
) {
    var visibleBooksLimit by rememberSaveable { mutableIntStateOf(Constants.BOOKS_TO_SHOW) }
    val booksToShow = books.books.take(visibleBooksLimit)
    val hasMore = books.books.size > visibleBooksLimit

    if (booksToShow.isNotEmpty()) {
        Column(modifier) {
            BooksSectionHeader(
                title = title,
                booksCount = books.books.size,
                showAll = onShowAll != null,
                onShowAll = { onShowAll?.invoke() },
            )
            Spacer(Modifier.height(8.dp))
            LazyRow {
                itemsIndexed(booksToShow) { index, book ->

                    val isFirst = index == 0 || !isSwitchingEnabled
                    val isLast = index == visibleBooksLimit - 1 ||
                        index == booksToShow.count() - 1 ||
                        !isSwitchingEnabled
                    VerticalBookItem(
                        book = book,
                        isSwitchLeftIconEnabled = !isFirst && book.isPending(),
                        isSwitchRightIconEnabled = !isLast && book.isPending(),
                        onClick = { onBookClick(book.id) },
                        onSwitchToLeft = {
                            onSwitchToLeft(index)
                        },
                        onSwitchToRight = {
                            onSwitchToRight(index)
                        },
                        onLongClick = { onLongClickBook(book) },
                    )
                }
                if (hasMore) {
                    item {
                        ShowMoreItems(
                            onClick = {
                                visibleBooksLimit += Constants.BOOKS_TO_SHOW
                            },
                            modifier = if (isSwitchingEnabled) {
                                Modifier.height(300.dp)
                            } else {
                                Modifier.height(205.dp)
                            },
                        )
                    }
                }
            }
            if (showDivider) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BooksSectionHeader(
    title: String,
    booksCount: Int,
    showAll: Boolean,
    onShowAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Badge(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary,
        ) {
            Text(
                text = booksCount.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.weight(1f))
        if (showAll) {
            TextButton(onClick = onShowAll) {
                Text(
                    text = stringResource(Res.string.show_all),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ShowMoreItems(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val text = stringResource(Res.string.show_more)
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .width(150.dp)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = text
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = rememberVectorPainter(Icons.Default.AddCircleOutline),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
internal fun getBoldTextFor(text: String, placeholder: String): AnnotatedString =
    buildAnnotatedString {
        val startIndex = text.indexOf(placeholder)
        if (startIndex != -1) {
            append(text.substring(0, startIndex))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(placeholder)
            }
            append(text.substring(startIndex + placeholder.length))
        } else {
            append(text)
        }
    }

@CustomPreviewLightDarkWithBackground
@Composable
private fun StartRatingBarPreview() {
    ReaderCollectionTheme {
        StarRatingBar(
            rating = 7f / 2,
            onRatingChanged = {},
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun SearchBarPreview() {
    ReaderCollectionTheme {
        SearchBar(text = "text", onSearch = {}, showLeadingIcon = true)
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun FilterChipPreview() {
    ReaderCollectionTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CustomFilterChip(
                title = "Value 1",
                selected = true,
                onClick = {},
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                selectedIcon = rememberVectorPainter(Icons.Default.Done)
                    .withDescription(null),
            )
            CustomFilterChip(
                title = "Value 2",
                selected = false,
                onClick = {},
            )
        }
    }
}

@CustomPreviewLightDark
@Composable
private fun CustomInputChipPreview() {
    ReaderCollectionTheme {
        CustomInputChip(
            text = "Value",
            endIcon = rememberVectorPainter(Icons.Default.Close)
                .withDescription(null),
        )
    }
}

@CustomPreviewLightDark
@Composable
private fun CustomOutlinedInputChipPreview() {
    ReaderCollectionTheme {
        CustomOutlinedInputChip(
            text = "Value",
            endIcon = rememberVectorPainter(Icons.Default.KeyboardArrowDown)
                .withDescription(null),
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun CustomCardPreview() {
    ReaderCollectionTheme {
        CustomCard(
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )
            },
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun EmptyStateCardPreview() {
    ReaderCollectionTheme {
        EmptyStateCard(
            subtitle = stringResource(Res.string.empty_library_description),
            action = StateCardData.Action(
                title = "Explorar catálogo",
                onClick = {},
            ),
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun NoResultsStateCardPreview() {
    ReaderCollectionTheme {
        NoResultsStateCard(query = "Obscure Title XYZ")
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun ErrorStateCardPreview() {
    ReaderCollectionTheme {
        ErrorStateCard(onRetry = {})
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun BooksSectionPreview() {
    val books = listOf(
        Book("1").copy(title = "Book 1"),
        Book("2").copy(title = "Book 2"),
    )
    ReaderCollectionTheme {
        BooksSection(
            title = stringResource(Res.string.pending),
            books = Books(books),
            isSwitchingEnabled = false,
            onShowAll = {},
            onBookClick = {},
            showDivider = false,
        )
    }
}

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Light-en", group = "Light", locale = "en")
@Preview(name = "Light-es", group = "Light", locale = "es")
@Preview(
    name = "Dark-en",
    group = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    locale = "en",
)
@Preview(
    name = "Dark-es",
    group = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    locale = "es",
)
annotation class CustomPreviewLightDark

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Light-en", group = "Light", locale = "en", heightDp = 3000)
@Preview(name = "Light-es", group = "Light", locale = "es", heightDp = 3000)
@Preview(
    name = "Dark-en",
    group = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    locale = "en",
    heightDp = 3000,
)
@Preview(
    name = "Dark-es",
    group = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    locale = "es",
    heightDp = 3000,
)
annotation class CustomPreviewLightDarkLong

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
@Preview(name = "Light-en", group = "Light", locale = "en", showBackground = true)
@Preview(name = "Light-es", group = "Light", locale = "es", showBackground = true)
@Preview(
    name = "Dark-en",
    group = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    locale = "en",
    showBackground = true,
)
@Preview(
    name = "Dark-es",
    group = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    locale = "es",
    showBackground = true,
)
annotation class CustomPreviewLightDarkWithBackground

@Composable
internal fun LaunchedEffectOnce(block: () -> Unit) {
    var isLaunched by rememberSaveable { mutableStateOf(false) }
    if (!isLaunched) {
        LaunchedEffect(block) {
            block()
            isLaunched = true
        }
    }
}