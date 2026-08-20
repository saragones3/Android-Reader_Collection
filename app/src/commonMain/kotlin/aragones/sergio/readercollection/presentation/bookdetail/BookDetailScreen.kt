/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/3/2025
 */

package aragones.sergio.readercollection.presentation.bookdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aragones.sergio.readercollection.data.remote.model.FORMATS
import aragones.sergio.readercollection.data.remote.model.FormatResponse
import aragones.sergio.readercollection.data.remote.model.GENRES
import aragones.sergio.readercollection.data.remote.model.GenreResponse
import aragones.sergio.readercollection.data.remote.model.STATES
import aragones.sergio.readercollection.data.remote.model.StateResponse
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.presentation.LocalLanguage
import aragones.sergio.readercollection.presentation.components.ButtonType
import aragones.sergio.readercollection.presentation.components.CustomCard
import aragones.sergio.readercollection.presentation.components.CustomDropdownMenu
import aragones.sergio.readercollection.presentation.components.CustomInputChip
import aragones.sergio.readercollection.presentation.components.CustomOutlinedInputChip
import aragones.sergio.readercollection.presentation.components.CustomOutlinedTextField
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDarkLong
import aragones.sergio.readercollection.presentation.components.CustomToolbar
import aragones.sergio.readercollection.presentation.components.DateCustomOutlinedTextField
import aragones.sergio.readercollection.presentation.components.DropdownOutlinedTextField
import aragones.sergio.readercollection.presentation.components.DropdownValues
import aragones.sergio.readercollection.presentation.components.ImageWithLoading
import aragones.sergio.readercollection.presentation.components.MainActionButton
import aragones.sergio.readercollection.presentation.components.MultilineCustomOutlinedTextField
import aragones.sergio.readercollection.presentation.components.StarRatingBar
import aragones.sergio.readercollection.presentation.components.TopAppBarIcon
import aragones.sergio.readercollection.presentation.components.withDescription
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import aragones.sergio.readercollection.presentation.theme.White
import aragones.sergio.readercollection.utils.UiDateMapper.getValueToShow
import aragones.sergio.readercollection.utils.UiDateMapper.toLocalDate
import com.aragones.sergio.util.BookState
import com.aragones.sergio.util.Constants
import com.aragones.sergio.util.CustomInputType
import com.aragones.sergio.util.extensions.currentLocalDate
import com.aragones.sergio.util.extensions.isNotBlank
import com.aragones.sergio.util.extensions.toLocalDate
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.add_author
import reader_collection.app.generated.resources.add_book
import reader_collection.app.generated.resources.add_genre
import reader_collection.app.generated.resources.add_isbn
import reader_collection.app.generated.resources.add_pages
import reader_collection.app.generated.resources.add_photo
import reader_collection.app.generated.resources.add_publisher
import reader_collection.app.generated.resources.add_summary
import reader_collection.app.generated.resources.add_synopsis
import reader_collection.app.generated.resources.add_title
import reader_collection.app.generated.resources.by_author
import reader_collection.app.generated.resources.cancel_changes
import reader_collection.app.generated.resources.clear_text
import reader_collection.app.generated.resources.delete
import reader_collection.app.generated.resources.edit_book
import reader_collection.app.generated.resources.format_title
import reader_collection.app.generated.resources.ic_default_book_cover_blue
import reader_collection.app.generated.resources.isbn
import reader_collection.app.generated.resources.pages
import reader_collection.app.generated.resources.published_date
import reader_collection.app.generated.resources.publisher
import reader_collection.app.generated.resources.reading_date
import reader_collection.app.generated.resources.remove_book_from_library
import reader_collection.app.generated.resources.save_changes
import reader_collection.app.generated.resources.select_a_date
import reader_collection.app.generated.resources.select_format
import reader_collection.app.generated.resources.select_state
import reader_collection.app.generated.resources.state
import reader_collection.app.generated.resources.summary
import reader_collection.app.generated.resources.synopsis

@Composable
fun BookDetailScreen(
    state: BookDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
    onSave: (Book) -> Unit,
    onChangeData: (Book) -> Unit,
    onSetImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center,
    ) {
        if (state.book == null) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else {
            var isImageVisible by remember { mutableStateOf(true) }
            val actionsTint = if (isImageVisible) {
                White
            } else {
                MaterialTheme.colorScheme.primary
            }
            val actions: @Composable RowScope.() -> Unit = when {
                state.isAlreadySaved && !state.isEditable -> {
                    {
                        TopAppBarIcon(
                            accessibilityPainter = rememberVectorPainter(Icons.Default.Edit)
                                .withDescription(stringResource(Res.string.edit_book)),
                            onClick = onEdit,
                            tint = actionsTint,
                        )
                    }
                }
                state.isAlreadySaved && state.isEditable -> {
                    {
                        TopAppBarIcon(
                            accessibilityPainter = rememberVectorPainter(Icons.Default.Cancel)
                                .withDescription(stringResource(Res.string.cancel_changes)),
                            onClick = onCancel,
                            tint = actionsTint,
                        )
                    }
                }
                else -> {
                    {}
                }
            }
            val scrollState = rememberScrollState()
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    ImageHeader(
                        imageUrl = state.book.thumbnail ?: state.book.image,
                        title = state.book.title,
                        state = state,
                        onSetImage = onSetImage,
                        onImageVisibilityChange = { isImageVisible = it },
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                    ) {
                        BookDetailContent(
                            book = state.book,
                            isEditable = state.isEditable,
                            onChangeData = onChangeData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 12.dp,
                                    top = 24.dp,
                                    end = 12.dp,
                                    bottom = 0.dp,
                                ),
                        )
                        if (state.isEditable || state.isAlreadySaved) {
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
                CustomToolbar(
                    title = "",
                    modifier = Modifier,
                    backgroundColor = Color.Transparent,
                    backTintColor = actionsTint,
                    onBack = onBack,
                    actions = actions,
                )
                if (state.isEditable || state.isAlreadySaved) {
                    BookDetailFooter(
                        isEditable = state.isEditable,
                        isAlreadySaved = state.isAlreadySaved,
                        onClick = { if (state.isEditable) onSave(state.book) else onRemove() },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageHeader(
    imageUrl: String?,
    title: String?,
    state: BookDetailUiState,
    onSetImage: () -> Unit,
    onImageVisibilityChange: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.height(400.dp),
    ) {
        ImageWithLoading(
            imageUrl = imageUrl,
            placeholder = Res.drawable.ic_default_book_cover_blue,
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    val yPosition = coordinates.positionInWindow().y
                    val height = coordinates.size.height
                    onImageVisibilityChange(yPosition + height > 0)
                },
            contentDescription = title,
            imagePadding = 48.dp,
        )
        if (state.isEditable) {
            FloatingActionButton(
                onClick = onSetImage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 8.dp),
                contentColor = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.AddAPhoto),
                    contentDescription = stringResource(Res.string.add_photo),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    isEditable: Boolean,
    onChangeData: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        MainBookInfo(
            title = book.title,
            authors = book.authorsToString(),
            categories = book.categories,
            isEditable = isEditable,
            onChangeData = { title, authors, categories ->
                onChangeData(
                    book.copy(
                        title = title,
                        authors = authors?.split(",")?.map { it.trimStart() },
                        categories = categories,
                    ),
                )
            },
        )
        Spacer(Modifier.height(24.dp))
        SectionContainer(title = stringResource(Res.string.synopsis)) {
            MultilineCustomOutlinedTextField(
                text = book.description.takeIf { it.isNotBlank() }.orElse(isEditable),
                labelText = "",
                onTextChanged = {
                    onChangeData(book.copy(description = it))
                },
                modifier = Modifier.fillMaxWidth(),
                placeholderText = stringResource(Res.string.add_synopsis),
                endIcon = rememberVectorPainter(Icons.Default.Clear)
                    .withDescription(stringResource(Res.string.clear_text))
                    .takeIf { isEditable && book.description?.isNotBlank() == true },
                maxLength = 10240,
                maxLines = 8,
                enabled = isEditable,
                onEndIconClicked = {
                    onChangeData(book.copy(description = null))
                }.takeIf { isEditable },
            )
        }
        SectionContainer(
            title = "",
            contentModifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            StarRatingBar(
                rating = book.rating.toFloat() / 2,
                onRatingChanged = {
                    if (isEditable) {
                        onChangeData(book.copy(rating = it.toDouble() * 2))
                    }
                },
                isSelectable = isEditable,
                starSize = 36.dp,
            )
        }
        SectionContainer(title = stringResource(Res.string.summary)) {
            MultilineCustomOutlinedTextField(
                text = book.summary.takeIf { it.isNotBlank() }.orElse(isEditable),
                labelText = "",
                onTextChanged = {
                    onChangeData(book.copy(summary = it))
                },
                modifier = Modifier.fillMaxWidth(),
                placeholderText = stringResource(Res.string.add_summary),
                endIcon = rememberVectorPainter(Icons.Default.Clear)
                    .withDescription(stringResource(Res.string.clear_text))
                    .takeIf { isEditable && book.summary?.isNotBlank() == true },
                maxLength = 10240,
                maxLines = 8,
                enabled = isEditable,
                onEndIconClicked = {
                    onChangeData(book.copy(summary = null))
                }.takeIf { isEditable },
            )
        }
        ReadingInfo(
            book = book,
            onChangeData = onChangeData,
            isEditable = isEditable,
        )
        PublishInfo(
            book = book,
            isEditable = isEditable,
            onChangeData = onChangeData,
        )
    }
}

@Composable
private fun BookDetailFooter(
    isEditable: Boolean,
    isAlreadySaved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = when {
        isEditable && isAlreadySaved -> stringResource(Res.string.save_changes)
        isEditable -> stringResource(Res.string.add_book)
        else -> stringResource(Res.string.remove_book_from_library)
    }
    val type = if (isEditable) {
        ButtonType.MAIN
    } else {
        ButtonType.DESTRUCTIVE
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        MainActionButton(
            text = text,
            enabled = true,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            type = type,
        )
    }
}

@Composable
private fun MainBookInfo(
    title: String?,
    authors: String,
    categories: List<GenreResponse>?,
    isEditable: Boolean,
    onChangeData: (String?, String?, List<GenreResponse>?) -> Unit,
) {
    if (isEditable) {
        CustomOutlinedTextField(
            text = title?.takeIf { it.isNotBlank() }.orElse(isEditable),
            labelText = "",
            onTextChanged = {
                onChangeData(it, authors, categories)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholderText = stringResource(Res.string.add_title),
            textStyle = MaterialTheme.typography.displayLarge,
            endIcon = rememberVectorPainter(Icons.Default.Clear)
                .withDescription(stringResource(Res.string.clear_text))
                .takeIf { isEditable && title?.isNotBlank() == true },
            inputType = CustomInputType.MULTI_LINE_TEXT,
            enabled = isEditable,
            onEndIconClicked = {
                onChangeData(null, authors, categories)
            }.takeIf { isEditable },
        )
        CustomOutlinedTextField(
            text = authors.takeIf { it.isNotBlank() }.orElse(isEditable),
            labelText = "",
            onTextChanged = {
                onChangeData(title, it, categories)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholderText = stringResource(Res.string.add_author),
            endIcon = rememberVectorPainter(Icons.Default.Clear)
                .withDescription(stringResource(Res.string.clear_text))
                .takeIf { isEditable && authors.isNotBlank() },
            inputType = CustomInputType.MULTI_LINE_TEXT,
            enabled = isEditable,
            onEndIconClicked = {
                onChangeData(title, null, categories)
            }.takeIf { isEditable },
        )
    } else {
        Spacer(Modifier.height(24.dp))
        Text(
            text = title?.takeIf { it.isNotBlank() }.orElse(isEditable),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(
                Res.string.by_author,
                authors.takeIf { it.isNotBlank() }.orElse(isEditable),
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(24.dp))
    }
    BookCategories(
        categories = categories ?: emptyList(),
        isEditable = isEditable,
        onChange = {
            onChangeData(title, authors, it)
        },
    )
}

@Composable
private fun BookCategories(
    categories: List<GenreResponse>,
    isEditable: Boolean,
    onChange: (List<GenreResponse>) -> Unit,
    modifier: Modifier = Modifier,
) {
    categories
        .map { it.name }
        .sorted()
        .let { categoryNames ->
            FlowRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                categoryNames.forEach { categoryName ->
                    CustomInputChip(
                        text = categoryName,
                        onEndIconClick = {
                            onChange(categories.filter { it.name != categoryName })
                        },
                        endIcon = rememberVectorPainter(Icons.Default.Close)
                            .withDescription(stringResource(Res.string.delete))
                            .takeIf { isEditable },
                    )
                }
                if (isEditable) {
                    InputChipWithDropdownMenu(
                        onChangeData = { newCategoryName ->
                            val currentCategories = categories.toMutableList()
                            if (currentCategories.any { it.name == newCategoryName }) {
                                return@InputChipWithDropdownMenu
                            }
                            GENRES.firstOrNull { it.name == newCategoryName }?.let {
                                currentCategories.add(it)
                            }
                            onChange(currentCategories)
                        },
                    )
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputChipWithDropdownMenu(onChangeData: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    CustomDropdownMenu(
        values = DropdownValues(GENRES.map { it.name }.sorted()),
        expanded = expanded,
        onExpand = {},
        onOptionSelected = {
            expanded = false
            onChangeData(it)
        },
    ) {
        CustomOutlinedInputChip(
            text = stringResource(Res.string.add_genre),
            onClick = { expanded = !expanded },
            onEndIconClick = { expanded = !expanded },
            endIcon = rememberVectorPainter(
                if (expanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
            ).withDescription(null),
        )
    }
}

@Composable
private fun ReadingInfo(book: Book, onChangeData: (Book) -> Unit, isEditable: Boolean) {
    val language = LocalLanguage.current
    val stateValues = STATES.map { it.name }
    val bookState =
        if (book.state == null) {
            stateValues.firstOrNull() ?: ""
        } else {
            val index = STATES.map { it.id }.indexOf(book.state)
            if (index != -1 && index < stateValues.size) {
                stateValues[index]
            } else {
                stateValues.firstOrNull() ?: ""
            }
        }
    val formatValues = FORMATS.map { it.name }
    val bookFormat =
        if (book.format == null) {
            formatValues.firstOrNull() ?: ""
        } else {
            val index = FORMATS.map { it.id }.indexOf(book.format)
            if (index != -1 && index < formatValues.size) {
                formatValues[index]
            } else {
                formatValues.firstOrNull() ?: ""
            }
        }

    SectionContainer(title = "") {
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            DropdownOutlinedTextField(
                currentValue = bookState,
                values = DropdownValues(stateValues),
                labelText = stringResource(Res.string.state),
                onOptionSelected = { newStateValue ->
                    val newStateId = STATES.firstOrNull { it.name == newStateValue }?.id
                    val newReadingDate = currentLocalDate().takeIf {
                        book.readingDate == null && newStateId == BookState.READ
                    } ?: book.readingDate
                    onChangeData(
                        book.copy(
                            state = newStateId,
                            readingDate = newReadingDate.toString().toLocalDate(),
                        ),
                    )
                },
                modifier = Modifier.weight(1f),
                placeholderText = stringResource(Res.string.select_state),
                enabled = isEditable,
            )
            DropdownOutlinedTextField(
                currentValue = bookFormat,
                values = DropdownValues(formatValues),
                labelText = stringResource(Res.string.format_title),
                onOptionSelected = { newFormatValue ->
                    val newFormatId = FORMATS.firstOrNull { it.name == newFormatValue }?.id
                    onChangeData(book.copy(format = newFormatId))
                },
                modifier = Modifier.weight(1f),
                placeholderText = stringResource(Res.string.select_format),
                enabled = isEditable,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            DateCustomOutlinedTextField(
                text = book.readingDate.getValueToShow(language)
                    ?: Constants.NO_VALUE.takeIf { !isEditable }
                    ?: Constants.EMPTY_VALUE,
                labelText = stringResource(Res.string.reading_date),
                onTextChanged = {
                    onChangeData(
                        book.copy(
                            readingDate = it.toLocalDate(language),
                            state = BookState.READ,
                        ),
                    )
                },
                modifier = Modifier.weight(2f),
                placeholderText = stringResource(Res.string.select_a_date),
                endIcon = rememberVectorPainter(Icons.Default.Clear)
                    .withDescription(stringResource(Res.string.clear_text))
                    .takeIf { isEditable && book.readingDate != null },
                enabled = isEditable,
                onEndIconClicked = {
                    onChangeData(book.copy(readingDate = null))
                }.takeIf { isEditable },
                language = language,
            )
            CustomOutlinedTextField(
                text = book.pageCount
                    .takeIf { it > 0 }
                    ?.toString()
                    .orElse(isEditable),
                labelText = stringResource(Res.string.pages),
                onTextChanged = { newPages ->
                    val pages = newPages.takeIf { it.isNotBlank() }?.toInt() ?: 0
                    onChangeData(book.copy(pageCount = pages))
                },
                modifier = Modifier.weight(1f),
                placeholderText = stringResource(Res.string.add_pages),
                endIcon = rememberVectorPainter(Icons.Default.Clear)
                    .withDescription(stringResource(Res.string.clear_text))
                    .takeIf { isEditable && book.pageCount > 0 },
                inputType = CustomInputType.NUMBER,
                maxLength = 5,
                enabled = isEditable,
                onEndIconClicked = {
                    onChangeData(book.copy(pageCount = 0))
                }.takeIf { isEditable },
            )
        }
    }
}

@Composable
private fun PublishInfo(book: Book, isEditable: Boolean, onChangeData: (Book) -> Unit) {
    val language = LocalLanguage.current
    SectionContainer(title = "") {
        Spacer(Modifier.height(12.dp))
        DateCustomOutlinedTextField(
            text = book.publishedDate.getValueToShow(language)
                ?: Constants.NO_VALUE.takeIf { !isEditable }
                ?: Constants.EMPTY_VALUE,
            labelText = stringResource(Res.string.published_date),
            onTextChanged = {
                onChangeData(
                    book.copy(
                        publishedDate = it.toLocalDate(language),
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            placeholderText = stringResource(Res.string.select_a_date),
            endIcon = rememberVectorPainter(Icons.Default.Clear)
                .withDescription(stringResource(Res.string.clear_text))
                .takeIf { isEditable && book.publishedDate != null },
            enabled = isEditable,
            onEndIconClicked = {
                onChangeData(book.copy(publishedDate = null))
            }.takeIf { isEditable },
            language = language,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomOutlinedTextField(
                text = book.publisher.takeIf { it.isNotBlank() }.orElse(isEditable),
                labelText = stringResource(Res.string.publisher),
                onTextChanged = {
                    onChangeData(book.copy(publisher = it))
                },
                modifier = Modifier.weight(1f),
                placeholderText = stringResource(Res.string.add_publisher),
                endIcon = rememberVectorPainter(Icons.Default.Clear)
                    .withDescription(stringResource(Res.string.clear_text))
                    .takeIf { isEditable && book.publisher?.isNotBlank() == true },
                inputType = CustomInputType.MULTI_LINE_TEXT,
                enabled = isEditable,
                onEndIconClicked = {
                    onChangeData(book.copy(publisher = null))
                }.takeIf { isEditable },
            )
            CustomOutlinedTextField(
                text = book.isbn.takeIf { it.isNotBlank() }.orElse(isEditable),
                labelText = stringResource(Res.string.isbn),
                onTextChanged = {
                    onChangeData(book.copy(isbn = it))
                },
                modifier = Modifier.weight(1f),
                placeholderText = stringResource(Res.string.add_isbn),
                endIcon = rememberVectorPainter(Icons.Default.Clear)
                    .withDescription(stringResource(Res.string.clear_text))
                    .takeIf { isEditable && book.isbn?.isNotBlank() == true },
                inputType = CustomInputType.NUMBER,
                enabled = isEditable,
                onEndIconClicked = {
                    onChangeData(book.copy(isbn = null))
                }.takeIf { isEditable },
            )
        }
    }
}

@Composable
private fun SectionContainer(
    title: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    CustomCard(
        modifier = modifier,
        contentModifier = contentModifier,
        content = {
            if (title.isNotBlank()) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(Modifier.height(12.dp))
            }
            content()
        },
    )
}

@CustomPreviewLightDarkLong
@Composable
fun BookDetailScreenPreview(
    @PreviewParameter(BookDetailScreenPreviewParameterProvider::class) state: BookDetailUiState,
) {
    FORMATS = listOf(FormatResponse("PHYSICAL", "Physical"))
    STATES = listOf(StateResponse("READING", "Reading"))
    ReaderCollectionTheme {
        CompositionLocalProvider(LocalLanguage provides "en") {
            BookDetailScreen(
                state = state,
                onBack = {},
                onEdit = {},
                onRemove = {},
                onCancel = {},
                onSave = {},
                onChangeData = {},
                onSetImage = {},
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            )
        }
    }
}

private class BookDetailScreenPreviewParameterProvider :
    PreviewParameterProvider<BookDetailUiState> {

    override val values: Sequence<BookDetailUiState>
        get() = sequenceOf(
            BookDetailUiState(
                book = Book("1").copy(
                    title = "Book title",
                    subtitle = "Book subtitle",
                    authors = listOf("Author1", "Author 2 with a long name"),
                    publisher = "Publisher",
                    description =
                        """
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                        incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                        exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute
                        irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
                        pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia
                        deserunt mollit anim id est laborum."
                        """.trimIndent(),
                    summary = "Summary",
                    pageCount = 100,
                    categories = listOf(
                        GenreResponse("1", "Category 1"),
                        GenreResponse("2", "Category 2"),
                        GenreResponse("3", "Category 3"),
                        GenreResponse("4", "Category 4"),
                        GenreResponse("5", "Category 5"),
                        GenreResponse("6", "Category 6"),
                    ),
                    averageRating = 7.0,
                    ratingsCount = 100,
                    rating = 5.0,
                    format = "PHYSICAL",
                    state = BookState.READING,
                ),
                isAlreadySaved = true,
                isEditable = false,
            ),
            BookDetailUiState(
                book = Book("1").copy(
                    title = "Book title",
                    subtitle = "Book subtitle",
                    authors = listOf("Author1", "Author 2 with a long name"),
                    publisher = "Publisher",
                    description = "Description",
                    summary = "Summary",
                    pageCount = 100,
                    categories = listOf(GenreResponse("", "Category")),
                    averageRating = 7.0,
                    ratingsCount = 100,
                    rating = 5.0,
                    format = "PHYSICAL",
                    state = BookState.READING,
                ),
                isAlreadySaved = true,
                isEditable = true,
            ),
            BookDetailUiState(
                book = Book("1").copy(
                    title = "Book title",
                    subtitle = "Book subtitle",
                    authors = listOf("Author1", "Author 2 with a long name"),
                    publisher = "Publisher",
                    pageCount = 100,
                    averageRating = 7.0,
                    ratingsCount = 100,
                    rating = 5.0,
                    format = "PHYSICAL",
                    state = BookState.READING,
                ),
                isAlreadySaved = false,
                isEditable = false,
            ),
            BookDetailUiState(
                book = null,
                isAlreadySaved = false,
                isEditable = false,
            ),
        )
}

private fun String?.orElse(isEditable: Boolean): String =
    this ?: Constants.NO_VALUE.takeIf { !isEditable } ?: Constants.EMPTY_VALUE