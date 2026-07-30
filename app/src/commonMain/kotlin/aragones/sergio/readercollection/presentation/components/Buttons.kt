/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 22/3/2024
 */

package aragones.sergio.readercollection.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.delete
import reader_collection.app.generated.resources.export_data
import reader_collection.app.generated.resources.import_data
import reader_collection.app.generated.resources.sign_in

@Composable
fun MainActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.MAIN,
) {
    val buttonColors = when (type) {
        ButtonType.MAIN -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        )
        ButtonType.DESTRUCTIVE -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
        )
    }
    val textColor = when (type) {
        ButtonType.MAIN -> MaterialTheme.colorScheme.secondary
        ButtonType.DESTRUCTIVE -> MaterialTheme.colorScheme.onError
    }
    Button(
        onClick = onClick,
        modifier = modifier.widthIn(max = 320.dp),
        enabled = enabled,
        colors = buttonColors,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            maxLines = 1,
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    startPainter: AccessibilityPainter? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
        ),
    ) {
        startPainter?.let {
            Icon(
                painter = it.painter,
                contentDescription = it.contentDescription,
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
        )
    }
}

@Composable
fun SecondaryOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    painter: AccessibilityPainter? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
    ) {
        painter?.let {
            Icon(
                painter = it.painter,
                contentDescription = it.contentDescription,
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
        )
    }
}

@Composable
fun ListButton(painter: AccessibilityPainter, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.padding(12.dp),
        contentColor = MaterialTheme.colorScheme.secondary,
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            painter = painter.painter,
            contentDescription = painter.contentDescription,
            tint = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
fun MainTextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier.widthIn(max = 320.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
    }
}

@CustomPreviewLightDark
@Composable
private fun MainActionButtonPreview() {
    ReaderCollectionTheme {
        MainActionButton(
            text = stringResource(Res.string.sign_in),
            enabled = true,
            onClick = {},
        )
    }
}

@CustomPreviewLightDark
@Composable
private fun MainDestructiveActionButtonPreview() {
    ReaderCollectionTheme {
        MainActionButton(
            text = stringResource(Res.string.delete),
            enabled = true,
            onClick = {},
            type = ButtonType.DESTRUCTIVE,
        )
    }
}

@CustomPreviewLightDark
@Composable
private fun SecondaryButtonPreview() {
    ReaderCollectionTheme {
        SecondaryButton(
            text = stringResource(Res.string.import_data),
            enabled = true,
            onClick = {},
            startPainter = rememberVectorPainter(Icons.Default.Upload).withDescription(null),
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun SecondaryOutlinedButtonPreview() {
    ReaderCollectionTheme {
        SecondaryOutlinedButton(
            text = stringResource(Res.string.export_data),
            enabled = true,
            onClick = {},
            painter = rememberVectorPainter(Icons.Default.Download).withDescription(null),
        )
    }
}

@CustomPreviewLightDark
@Composable
private fun ListButtonPreview() {
    ReaderCollectionTheme {
        ListButton(
            painter = rememberVectorPainter(Icons.Default.KeyboardDoubleArrowUp)
                .withDescription(null),
            onClick = {},
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun MainTextButtonPreview() {
    ReaderCollectionTheme {
        MainTextButton(
            text = "Log-in",
            onClick = {},
        )
    }
}

enum class ButtonType {
    MAIN,
    DESTRUCTIVE,
}