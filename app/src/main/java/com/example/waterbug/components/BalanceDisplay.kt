package com.example.waterbug.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.example.waterbug.appstate.AppViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.waterbug.R
import com.example.waterbug.appstate.Balance
import com.example.waterbug.appstate.Mode
import com.example.waterbug.appstate.asset1
import com.example.waterbug.ui.theme.WaterbugTheme

@Composable
fun BalanceDisplay(activeBalance: Balance, onRefresh: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var mode by remember { mutableStateOf(Mode.TRANSPARENT) }

    Surface(
        shape = MaterialTheme.shapes.small,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ModeToggle(mode, {
                mode = when (it) {
                    0 -> Mode.TRANSPARENT
                    1 -> Mode.SHIELDED
                    else -> Mode.INDETERMINATE
                }
            })

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                contentAlignment = Alignment.TopCenter, modifier = Modifier.width(280.dp).height(140.dp),
            ) {
                ArcMeter(asset1 = activeBalance.transparentBalance,
                    asset2 = activeBalance.shieldedBalance,
                    mode = mode,
                    isLoading = isLoading,
                    progress = progress,
                    onRefresh = {
                        isLoading = true
                        onRefresh()
                    })
            }
        }
    }
}

@Composable
fun ArcMeter(
    asset1: Double,
    asset2: Double,
    mode: Mode,
    isLoading: Boolean,
    progress: Float, // Progress for loading mode (0.0 to 1.0)
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit
) {
    val total = asset1 + asset2
    val asset1Ratio = if (total > 0) asset1 / total else 0f
    val asset2Ratio = 1f - asset1Ratio.toFloat()
    val density = LocalDensity.current
    val strokeWidth = with(density) { 20.dp.toPx() }

    Box(
        contentAlignment = Alignment.Center, modifier = modifier.requiredSize(240.dp).offset(y = 60.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            val startAngle = 180f // Start from the left
            val sweepAngle = 180f // Semi-circle

            // Background arc
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
            )

            if (isLoading) {
                // Loading progress arc
                drawArc(
                    color = Color.Blue.copy(alpha = 0.5f),
                    startAngle = startAngle,
                    sweepAngle = progress * sweepAngle,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                )
            } else {
                // Asset1 arc
                drawArc(
                    color = if (mode == Mode.TRANSPARENT) Color.Yellow else Color.Yellow.copy(alpha = 0.4f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * asset1Ratio.toFloat(),
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                )

                // Asset2 arc
                drawArc(
                    color = if (mode == Mode.SHIELDED) Color.Cyan else Color.Cyan.copy(alpha = 0.4f),
                    startAngle = startAngle + sweepAngle * asset1Ratio.toFloat(),
                    sweepAngle = sweepAngle * asset2Ratio,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                )
            }
        }

        // Position the TextButton inside the semi-circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp), // Adjust padding to place it inside the semi-circle
            contentAlignment = Alignment.TopCenter
        ) {
            TextButton(onClick = onRefresh) {
                Text(
                    text = "Tap to\nShielded-Sync",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BalanceDisplayPreview() {
    val viewModel = AppViewModel()
    viewModel.loadTestData()
    WaterbugTheme(darkTheme = true) {
        Surface {
            BalanceDisplay(asset1.balances, { null })
        }
    }
}