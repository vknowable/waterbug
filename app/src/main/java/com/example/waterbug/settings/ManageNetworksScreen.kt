package com.example.waterbug.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.Account
import com.example.waterbug.appstate.AppState
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.appstate.Network
import com.example.waterbug.components.AccountList
import com.example.waterbug.components.NetworkList
import com.example.waterbug.ui.theme.WaterbugTheme

@Composable
fun ManageNetworksScreen(viewModel: AppViewModel, navController: NavController) {
    when (val appState = viewModel.appState.collectAsState().value) {
        is AppState.Loading -> {
            Text("Loading...", style = MaterialTheme.typography.titleLarge)
        }
        is AppState.Error -> {
            Text("Error loading data", style = MaterialTheme.typography.titleLarge)
        }
        is AppState.NetworkLoading -> {
            Text("Switching networks...", style = MaterialTheme.typography.titleLarge)
        }
        is AppState.NetworkError -> {
            Text("Network error")
            ManageNetworks(appState.lastKnownData.networks, appState.lastKnownData.activeNetworkIndex, { viewModel.switchNetwork(it) }, navController)
        }
        is AppState.DataLoaded -> {
            ManageNetworks(appState.networks, appState.activeNetworkIndex, { viewModel.switchNetwork(it) }, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageNetworks(networks: List<Network>, activeNetworkIndex: Int, setActiveNetworkIndex: (Int) -> Unit, navController: NavController) {
    WaterbugTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manage Networks") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // maybe text should be moved outside or add button moved inside list component
                NetworkList(networks, setActiveNetworkIndex, activeNetworkIndex) {
                    navController.navigate(
                        "edit_network/${it}"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    navController.navigate("add_network?returnRoute=networks")
                }) {
                    Text("Add New Network")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Close")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageNetworksScreenPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    ManageNetworksScreen(viewModel = viewModel, navController = rememberNavController())
}