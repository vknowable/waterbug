package com.example.waterbug.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterbug.R
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.appstate.Mode
import com.example.waterbug.ui.theme.WaterbugTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeToggle(mode: Mode, onClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val selectedIndex = if (mode == Mode.TRANSPARENT) 0 else 1
    val enabled = mode != Mode.INDETERMINATE
    val options = listOf("Transparent", "Shielded")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                colors = SegmentedButtonDefaults.colors().copy(activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = RoundedCornerShape(8.dp),
                ),
                onClick = {
                    onClick(index)
                },
                selected = index == selectedIndex,
                label = { Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    ) },
                icon = {
                    Icon(
                    painter = painterResource(id =
                        if (index == 0) {
                            R.drawable.baseline_visibility_24
                        } else R.drawable.baseline_shield_24),
                    contentDescription = "mode",
                    tint = if (index == selectedIndex) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    },
                ) },
                enabled = enabled,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModeTogglePreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            ModeToggle(mode = Mode.TRANSPARENT, onClick = { null })
        }
    }
}