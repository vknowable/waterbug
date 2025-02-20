package com.example.waterbug.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.R
import com.example.waterbug.ui.theme.WaterbugTheme

@Composable
fun TopBarActions(navController: NavController): @Composable (RowScope.() -> Unit) = {
    IconButton(onClick = { /* Handle history action */ }) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_history_24),
            contentDescription = "History"
        )
    }
    IconButton(onClick = { navController.navigate("settings") }) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarActionsPreview() {
    WaterbugTheme {
        Surface {
            TopBarActions(rememberNavController())
        }
    }
}