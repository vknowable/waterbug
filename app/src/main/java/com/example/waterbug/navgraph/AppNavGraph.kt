package com.example.waterbug.navgraph

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.waterbug.account.AddAccountScreen
import com.example.waterbug.account.EditAccountScreen
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.components.CustomSnackbarHost
import com.example.waterbug.home.HomeScreen
import com.example.waterbug.home.SendScreen
import com.example.waterbug.home.ShieldScreen
import com.example.waterbug.network.AddNetworkScreen
import com.example.waterbug.network.EditNetworkScreen
import com.example.waterbug.qrscanner.QRScannerScreen
import com.example.waterbug.settings.ManageAccountsScreen
import com.example.waterbug.settings.ManageNetworksScreen
import com.example.waterbug.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(navController: NavHostController, appViewModel: AppViewModel = viewModel()) {
    // shared snackbar state to display status messages across screens
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { CustomSnackbarHost(hostState = snackbarHostState, bottomPadding = 32.dp) },
        topBar = { TopAppBar(title = { null }, modifier = Modifier.height(0.dp)) },
        bottomBar = {
            BottomAppBar(
                content = { null },
                modifier = Modifier.height(0.dp)
            )
        }) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = appViewModel, navController = navController, snackbarHostState = snackbarHostState,
                )
            }
            composable("settings") {
                SettingsScreen(
                    navController = navController
                )
            }
            composable("accounts") {
                ManageAccountsScreen(
                    viewModel = appViewModel, navController = navController
                )
            }
            composable("networks") {
                ManageNetworksScreen(
                    viewModel = appViewModel, navController = navController
                )
            }
            composable("send") {
                SendScreen(
                    viewModel = appViewModel, navController = navController
                )
            }
            composable("shield") {
                ShieldScreen(
                    viewModel = appViewModel, navController = navController
                )
            }
            composable(
                route = "add_account?returnRoute={returnRoute}",
                arguments = listOf(navArgument("returnRoute") {
                    defaultValue = "home"
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val returnRoute = backStackEntry.arguments?.getString("returnRoute") ?: "home"
                AddAccountScreen(
                    viewModel = appViewModel,
                    navController = navController,
                    returnRoute = returnRoute,
                    snackbarHostState = snackbarHostState
                )
            }
            composable("edit_account/{account}") { backStackEntry ->
                val account = backStackEntry.arguments?.getString("account")?.toInt() ?: 1
                EditAccountScreen(
                    viewModel = appViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    accountIndex = account,
                )
            }
            composable(
                route = "add_network?returnRoute={returnRoute}",
                arguments = listOf(navArgument("returnRoute") {
                    defaultValue = "home"
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val returnRoute = backStackEntry.arguments?.getString("returnRoute") ?: "home"
                AddNetworkScreen(
                    viewModel = appViewModel,
                    navController = navController,
                    returnRoute = returnRoute,
                    snackbarHostState = snackbarHostState
                )
            }
            composable("edit_network/{network}") { backStackEntry ->
                val network = backStackEntry.arguments?.getString("network")?.toInt() ?: 1
                EditNetworkScreen(
                    viewModel = appViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    networkIndex = network,
                )
            }
            composable("qr_scanner") {
                QRScannerScreen(navController = navController, onResult = { scannedResult ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "scannedResult", scannedResult
                    )
                    navController.popBackStack()
                })
            }
        }
    }
}