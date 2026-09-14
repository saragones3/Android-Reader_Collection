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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import aragones.sergio.readercollection.presentation.theme.titleMediumEmphasized
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.delete
import reader_collection.app.generated.resources.export_data
import reader_collection.app.generated.resources.import_data
import reader_collection.app.generated.resources.logout_title
import reader_collection.app.generated.resources.sign_in

@Composable
fun MainActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
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
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = buttonColors,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = textColor,
            maxLines = 1,
        )
    }
}

@Composable
fun MainOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.MAIN,
    painter: AccessibilityPainter? = null,
    enabled: Boolean = true,
) {
    val buttonColors = when (type) {
        ButtonType.MAIN -> ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        )
        ButtonType.DESTRUCTIVE -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onError,
        )
    }
    val textColor = when (type) {
        ButtonType.MAIN -> MaterialTheme.colorScheme.primary
        ButtonType.DESTRUCTIVE -> MaterialTheme.colorScheme.error
    }
    val borderColor = when (type) {
        ButtonType.MAIN -> MaterialTheme.colorScheme.primary
        ButtonType.DESTRUCTIVE -> MaterialTheme.colorScheme.error
    }
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = buttonColors,
        border = BorderStroke(width = 1.dp, color = borderColor),
    ) {
        painter?.let {
            Icon(
                painter = it.painter,
                contentDescription = it.contentDescription,
                tint = textColor,
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMediumEmphasized,
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
    endPainter: AccessibilityPainter? = null,
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
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
        )
        endPainter?.let {
            Spacer(Modifier.weight(1f))
            Icon(
                painter = it.painter,
                contentDescription = it.contentDescription,
            )
        }
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
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
        )
    }
}

@Composable
fun ListButton(painter: AccessibilityPainter, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.padding(12.dp),
        shape = MaterialTheme.shapes.medium,
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
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
    }
}

@Composable
fun MainIconButton(
    painter: AccessibilityPainter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Icon(
            painter = painter.painter,
            contentDescription = painter.contentDescription,
            tint = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
fun SecondaryIconButton(
    painter: AccessibilityPainter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
    ) {
        Icon(
            painter = painter.painter,
            contentDescription = painter.contentDescription,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@CustomPreviewLightDark
@Composable
private fun MainActionButtonPreview() {
    ReaderCollectionTheme {
        MainActionButton(
            text = stringResource(Res.string.sign_in),
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
            onClick = {},
            type = ButtonType.DESTRUCTIVE,
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun MainOutlinedButtonPreview() {
    ReaderCollectionTheme {
        MainOutlinedButton(
            text = stringResource(Res.string.logout_title),
            onClick = {},
            painter = rememberVectorPainter(
                Icons.AutoMirrored.Default.Logout,
            ).withDescription(null),
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun MainDestructiveOutlinedButtonPreview() {
    ReaderCollectionTheme {
        MainOutlinedButton(
            text = stringResource(Res.string.logout_title),
            onClick = {},
            type = ButtonType.DESTRUCTIVE,
            painter = rememberVectorPainter(
                Icons.AutoMirrored.Default.Logout,
            ).withDescription(null),
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

@CustomPreviewLightDarkWithBackground
@Composable
private fun MainIconButtonPreview() {
    ReaderCollectionTheme {
        MainIconButton(
            painter = rememberVectorPainter(Icons.Default.Check)
                .withDescription(null),
            onClick = {},
        )
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun SecondaryIconButtonPreview() {
    ReaderCollectionTheme {
        SecondaryIconButton(
            painter = rememberVectorPainter(Icons.Default.Close)
                .withDescription(null),
            onClick = {},
        )
    }
}

enum class ButtonType {
    MAIN,
    DESTRUCTIVE,
}