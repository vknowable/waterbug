package com.example.waterbug.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.ui.theme.WaterbugTheme
import com.example.waterbug.utils.epochDurRemaining

@Composable
fun EpochDisplay(epoch: Int, secsToNextEpoch: Int, rewardsPerEpoch: Double) {
    val progress = 0.42F
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp, horizontal = 40.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Est. Shielded Rewards:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            )
            Text(
                text = String.format("%.2f", rewardsPerEpoch),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "NAM",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
                .height(20.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                    shape = MaterialTheme.shapes.extraSmall
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.extraSmall
                ),
        ) {
//                .clip(RoundedCornerShape(4.dp))
//                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .alpha(0.1F),
            )
            Text(
                text = "${epochDurRemaining(secsToNextEpoch)} remaining in Epoch $epoch",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
            )
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(4.dp), // Adds spacing between elements
//                modifier = Modifier.padding(4.dp) // Adds padding around the column
//            ) {
//                Text(
//                    text = "${epochDurRemaining(secsToNextEpoch)} remaining in Epoch $epoch",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//            }
        }
    }
}

@Preview
@Composable
fun EpochDisplayPreview() {
    val viewModel = AppViewModel()
    viewModel.loadTestData()
    WaterbugTheme(darkTheme = true) {
        Surface {
            EpochDisplay(47, 2365, 25.74)
        }
    }
}