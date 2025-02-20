package com.example.waterbug.network

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.appstate.Network
import com.example.waterbug.ui.theme.WaterbugTheme

@Composable
fun EnterDetails(
    step: Int,
    setStep: (Int) -> Unit,
    setIsProcessing: (Boolean) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    rpc: String,
    onRpcChange: (String) -> Unit,
    indexer: String,
    onIndexerChange: (String) -> Unit,
    masp: String,
    onMaspChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Network name", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Enter a human-readable name") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Chain Id", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = "some.chain",
            onValueChange = { null },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.labelMedium,
        )

        Text("Rpc URL", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = rpc,
            onValueChange = onRpcChange,
            placeholder = { Text("Enter a human-readable name") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Indexer URL", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = indexer,
            onValueChange = onIndexerChange,
            placeholder = { Text("Enter a human-readable name") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Masp-Indexer URL", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = masp,
            onValueChange = onMaspChange,
            placeholder = { Text("Enter a human-readable name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                setIsProcessing(true)
                setStep(step + 1)
            },
        ) {
            Text("Next")
        }
    }
}

@Composable
fun CheckNetwork(
    step: Int,
    setStep: (Int) -> Unit,
    setIsProcessing: (Boolean) -> Unit,
    setNetworkOk: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        // Placeholder: Simulate delay
        kotlinx.coroutines.delay(3000L)
        setIsProcessing(false)
        // placeholder: check network
        setNetworkOk(false)
        setStep(step + 1)

        // Perform account initialization
    }
    Column {
        Text("Some animation here")
        Text("Progress bar")
    }
}

@Composable
fun Results(setStep: (Int) -> Unit, networkOk: Boolean, onSave: () -> Unit, onExit: () -> Unit) {
    var isChecked by remember { mutableStateOf(false) }
    Column {
        when (networkOk) {
            true -> {
                Text("a logo")
                Text("Network configured is valid!")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Make this the active network")
                }
                Button(onClick = {
                    onSave()
                }) {
                    Text("Save")
                }
            }

            false -> {
                Text("The network configuration has some problems.")
                TextButton(onClick = {
                    setStep(1)
                }) {
                    Text("Edit configuration")
                }

                TextButton(onClick = {
                    // save network
                    onSave()
                }) {
                    Text("Add anyway")
                }

                TextButton(onClick = {
                    // exit without saving
                    onExit()
                }) {
                    Text("Exit")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNetworkScreen(
    viewModel: AppViewModel,
    navController: NavController,
    returnRoute: String,
    step: Int = 1,
    snackbarHostState: SnackbarHostState,
) {
    var step by remember { mutableIntStateOf(step) }
    var name by remember { mutableStateOf("") }
    var rpc by remember { mutableStateOf("") }
    var indexer by remember { mutableStateOf("") }
    var masp by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var networkOk by remember { mutableStateOf(false) }

    BackHandler {
        if (isProcessing) return@BackHandler
        // special behaviour for third screen, it should skip back over the check network screen
        when (step) {
            3 -> step = 1
            else -> {
                navController.navigate(returnRoute) {
                    popUpTo("add_network?returnRoute=$returnRoute") { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    val titleText = when (step) {
        1 -> "Add a new network provider"
        2 -> "Checking network configuration"
        3 -> "Checking network configuration"
        else -> ""
    }

    WaterbugTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                Column {
                    Text(titleText)
                    if (step <= 3) Text(
                        "Step $step of 3", style = MaterialTheme.typography.labelMedium
                    )
                }

            }, navigationIcon = {
                if (isProcessing) {
                    null
                } else {
                    IconButton(onClick = {
                        if (step == 3) {
                            step = 1
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            })
        }) {
            Surface(
                modifier = Modifier.padding(it),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp,
            ) {
                when (step) {
                    1 -> EnterDetails(
                        step = step,
                        setStep = { step = it },
                        setIsProcessing = { isProcessing = it },
                        name = name,
                        onNameChange = { name = it },
                        rpc = rpc,
                        onRpcChange = { rpc = it },
                        indexer = indexer,
                        onIndexerChange = { indexer = it },
                        masp = masp,
                        onMaspChange = { masp = it },
                    )

                    2 -> CheckNetwork(
                        step = step,
                        setStep = { step = it },
                        setIsProcessing = { isProcessing = it },
                        setNetworkOk = { networkOk = it },
                    )

//                    3 -> Results(navController, returnRoute, { step = it }, networkOk)
                    3 -> Results(
                        setStep = { step = it },
                        networkOk = false,
                        onSave = {
                            viewModel.upsertNetwork(
                                editIndex = null,
                                network = Network(
                                    name = name,
                                    chainId = "( Unknown )",
                                    rpcUrl = rpc,
                                    indexerUrl = indexer,
                                    maspIndexerUrl = masp,
                                ),
                                snackbarHostState = snackbarHostState,
                            )
                            navController.navigate(returnRoute) {
                                popUpTo("add_network?returnRoute=$returnRoute") { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onExit = {
                            navController.navigate(returnRoute) {
                                popUpTo("add_network?returnRoute=$returnRoute") { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnterDetailsPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme {
        Surface {
            AddNetworkScreen(viewModel, rememberNavController(), "", 1, SnackbarHostState())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckNetwork() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme {
        Surface {
            AddNetworkScreen(viewModel, rememberNavController(), "", 2, SnackbarHostState())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Results() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme {
        Surface {
            AddNetworkScreen(viewModel, rememberNavController(), "", 3, SnackbarHostState())
        }
    }
}
