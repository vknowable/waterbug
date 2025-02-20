package com.example.waterbug.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.R
import com.example.waterbug.appstate.Mode
import com.example.waterbug.ui.theme.WaterbugTheme

@Composable
fun BottomBarButtons(navController: NavController): @Composable (RowScope.() -> Unit) = {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp) // Fixed height for the bottom bar
            .background(color = Color.Transparent)
//            .fillMaxHeight()
//            .background(
//                color = MaterialTheme.colorScheme.background.copy(alpha = 0.75f) // Slightly darker background
//            )
//            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))) // Top border
            .padding(0.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Segment Buttons
        val items = listOf(
            Triple("Send", R.drawable.send_1, "send"),
            Triple("Receive", R.drawable.receive_1, "home"),
            Triple("Un/Shield", R.drawable.shield_minimalistic, "shield"),
            Triple("Scan QR", R.drawable.baseline_qr_code_scanner_24, "qr_scanner")
        )

        items.forEachIndexed { index, (text, iconResId, route) ->
            Box(
                modifier = Modifier
                    .weight(1f) // Divide the bar evenly
                    .fillMaxHeight()
//                    .clickable(enabled = mode != Mode.INDETERMINATE) {
                    .clickable {
                        navController.navigate(route)
                    },
//                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icon
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = text,
                        tint = Color.White.copy(alpha = 0.35f), // Low opacity watermark
                        modifier = Modifier
                            .size(32.dp) // Icon size
                            .align(Alignment.CenterHorizontally)
                    )
                    // Text underneath
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp)
                    )
                }
            }
            // Vertical bar separator (not after the last item)
            if (index < items.lastIndex) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarButtonsPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            Row {
                BottomBarButtons(rememberNavController()).invoke(this)
            }
        }
    }
}