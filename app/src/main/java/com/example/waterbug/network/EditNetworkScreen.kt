package com.example.waterbug.network

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.appstate.Network
import com.example.waterbug.ui.theme.WaterbugTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNetworkScreen(
    viewModel: AppViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    networkIndex: Int
) {
    val network = viewModel.getNetworks().getOrNull(networkIndex)
    var name by remember { mutableStateOf(network?.name ?: "") }
    var rpcUrl by remember { mutableStateOf(network?.rpcUrl ?: "") }
    var indexerUrl by remember { mutableStateOf(network?.indexerUrl ?: "") }
    var maspIndexerUrl by remember { mutableStateOf(network?.maspIndexerUrl ?: "") }

    WaterbugTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Network") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = topBarDelete(viewModel, navController, snackbarHostState, networkIndex)
                )
            }
        ) { contentPadding ->
            Surface(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Section Title
                    Text(
                        text = "Network Details",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Network Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Network Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Network Icon"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chain ID
                    OutlinedTextField(
                        value = network?.chainId ?: "( Unknown )",
                        onValueChange = { },
                        label = { Text("Chain ID") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info Icon"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // RPC URL
                    OutlinedTextField(
                        value = rpcUrl,
                        onValueChange = { rpcUrl = it },
                        label = { Text("RPC URL") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "RPC URL Icon"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Indexer URL
                    OutlinedTextField(
                        value = indexerUrl,
                        onValueChange = { indexerUrl = it },
                        label = { Text("Indexer URL") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Indexer Icon"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // MASP Indexer URL
                    OutlinedTextField(
                        value = maspIndexerUrl,
                        onValueChange = { maspIndexerUrl = it },
                        label = { Text("MASP Indexer URL") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "MASP Indexer Icon"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (network != null) {
                                    viewModel.upsertNetwork(
                                        editIndex = networkIndex,
                                        network = network.copy(
                                            name = name,
                                            rpcUrl = rpcUrl,
                                            indexerUrl = indexerUrl,
                                            maspIndexerUrl = maspIndexerUrl
                                        ),
                                        snackbarHostState = snackbarHostState
                                    )
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = name.isNotEmpty() &&
                                    rpcUrl.isNotEmpty() &&
                                    indexerUrl.isNotEmpty() &&
                                    maspIndexerUrl.isNotEmpty()
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun topBarDelete(
    viewModel: AppViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    networkIndex: Int
): @Composable (RowScope.() -> Unit) = {
    var showDialog by remember { mutableStateOf(false) }

    TextButton(onClick = { showDialog = true }) {
        Text("Delete")
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = {
                Column {
                    Text("Are you sure you want to delete this network?")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNetwork(
                            networkIndex,
                            snackbarHostState,
                        )
                        navController.popBackStack()
                        showDialog = false
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            })
    }
}

@Preview
@Composable
fun EditNetworkScreenPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme {
        Surface {
            EditNetworkScreen(viewModel, rememberNavController(), SnackbarHostState(), 0)
        }
    }
}