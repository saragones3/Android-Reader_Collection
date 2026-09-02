/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 11/8/2026
 */

package aragones.sergio.readercollection.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.app_theme_values
import reader_collection.app.generated.resources.english
import reader_collection.app.generated.resources.spanish

@Composable
fun OptionsSelector(options: List<OptionData>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            options.forEach { option ->
                Option(
                    data = option,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun Option(data: OptionData, modifier: Modifier = Modifier) {
    val backgroundColor by animateColorAsState(
        if (data.isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )
    val contentColor =
        if (data.isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.tertiary
        }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable { data.onClick() },
        color = backgroundColor,
        border = if (data.isSelected) {
            BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            )
        } else {
            null
        },
        shape = RoundedCornerShape(8.dp),
        shadowElevation = if (data.isSelected) 2.dp else 0.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (data.icon != null) {
                Icon(
                    painter = data.icon.painter,
                    contentDescription = data.icon.contentDescription,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = data.text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (data.isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor,
                maxLines = 1,
            )
        }
    }
}

data class OptionData(
    val text: String,
    val icon: AccessibilityPainter? = null,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)

@CustomPreviewLightDarkWithBackground
@Composable
private fun ThemeOptionsSelectorPreview() {
    val options = listOf(
        OptionData(
            text = stringArrayResource(Res.array.app_theme_values)[1],
            icon = rememberVectorPainter(Icons.Default.LightMode).withDescription(null),
            isSelected = false,
            onClick = {},
        ),
        OptionData(
            text = stringArrayResource(Res.array.app_theme_values)[2],
            icon = rememberVectorPainter(Icons.Default.DarkMode).withDescription(null),
            isSelected = true,
            onClick = {},
        ),
        OptionData(
            text = stringArrayResource(Res.array.app_theme_values)[0],
            icon = rememberVectorPainter(Icons.Default.Brightness4).withDescription(null),
            isSelected = false,
            onClick = {},
        ),
    )
    ReaderCollectionTheme {
        Column(Modifier.padding(16.dp)) {
            OptionsSelector(options)
        }
    }
}

@CustomPreviewLightDarkWithBackground
@Composable
private fun LanguageOptionsSelectorPreview() {
    val options = listOf(
        OptionData(
            text = stringResource(Res.string.english),
            isSelected = true,
            onClick = {},
        ),
        OptionData(
            text = stringResource(Res.string.spanish),
            isSelected = false,
            onClick = {},
        ),
    )
    ReaderCollectionTheme {
        Column(Modifier.fillMaxSize()) {
            OptionsSelector(options)
        }
    }
}
