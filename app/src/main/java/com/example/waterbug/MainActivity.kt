package com.example.waterbug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.ui.theme.WaterbugTheme
import com.example.waterbug.navgraph.AppNavGraph


//val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
//val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)
//
//if (isFirstRun) {
//    // Trigger new account flow
//    sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
//}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = AppViewModel()
        // still need to test/handle case where state is empty (index overflow errors)
//        viewModel.loadTestData()
        viewModel.initializeApp()
        enableEdgeToEdge()
        setContent {
            WaterbugTheme(darkTheme = true) {
                val navController = rememberNavController()
                AppNavGraph(navController = navController, appViewModel = viewModel)
            }
        }
    }
}
