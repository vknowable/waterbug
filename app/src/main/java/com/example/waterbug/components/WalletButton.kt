package com.example.waterbug.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.waterbug.R
import com.example.waterbug.appstate.Asset
import com.example.waterbug.appstate.asset1
import com.example.waterbug.ui.theme.WaterbugTheme

@Composable
fun WalletButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    WaterbugTheme {
        Button(onClick = onClick, enabled = enabled) {
            Text(text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletButtonPreview() {
    WaterbugTheme {
        WalletButton("Send", { null })
    }
}