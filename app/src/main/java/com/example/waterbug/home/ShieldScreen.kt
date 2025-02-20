package com.example.waterbug.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.appstate.Mode
import com.example.waterbug.components.AssetModalSelector
import com.example.waterbug.components.ModeToggle
import com.example.waterbug.ui.theme.WaterbugTheme
import com.example.waterbug.utils.truncateAddress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShieldScreen(viewModel: AppViewModel, navController: NavController) {
    var receiver by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var isProcessed by remember { mutableStateOf(false) }
    var transactionResult by remember { mutableStateOf<String?>(null) }

    // Placeholder
    val mode = Mode.TRANSPARENT
    val activeBalance = viewModel.getActiveAssetOrNull()?.balances

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shield") },
                navigationIcon = {
                    IconButton(onClick = { if (!isProcessing) navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier.padding(it),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
        ) {
            if (isProcessing || isProcessed) {
                ShieldProcessingView(transactionResult)
            } else {
                ShieldForm(
                    viewModel = viewModel,
                    receiver = receiver,
                    onReceiverChange = { receiver = it },
                    amount = amount,
                    onAmountChange = {
                        amount = it
                        val balance = if (mode == Mode.TRANSPARENT) {
                            activeBalance?.transparentBalance ?: 0.0
                        } else {
                            activeBalance?.shieldedBalance ?: 0.0
                        }
                        errorMessage = validateShieldBalance(it, balance)
                    },
                    memo = memo,
                    onMemoChange = { memo = it },
                    errorMessage = errorMessage,
                    onSubmit = {
                        isProcessing = true
                        simulateShieldTransaction { result ->
                            transactionResult = result
                            isProcessing = false
                            isProcessed = true
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}

fun validateShieldBalance(amount: String, balance: Double): String {
    val errorMessage = if (amount.toDoubleOrNull() ?: 0.0 > balance) {
        "Insufficient balance"
    } else {
        ""
    }
    return errorMessage
}

@Composable
fun ShieldForm(
    viewModel: AppViewModel,
    receiver: String,
    onReceiverChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    memo: String,
    onMemoChange: (String) -> Unit,
    errorMessage: String,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val appState = viewModel.appState.collectAsState().value
    // placeholder
    val mode = Mode.TRANSPARENT
    val activeAsset = viewModel.getActiveAssetOrNull()
    val activeAccount = viewModel.getActiveAccountOrNull()
    val balance = if (mode == Mode.TRANSPARENT) {
        activeAsset?.balances?.transparentBalance ?: 0.0
    } else {
        activeAsset?.balances?.shieldedBalance ?: 0.0
    }

    // Watch for appState changes and run validation
    LaunchedEffect(appState) {
        // re-validate amount
        onAmountChange(amount)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // move to top bar
        Text(text = "Shield", style = MaterialTheme.typography.titleMedium)
        // placeholder
        ModeToggle(mode, { null })
        if (activeAccount != null) {
            AssetModalSelector(
                activeAccount.assets,
                { viewModel.setActiveAssetIndex(it) },
                activeAccount.activeAssetIndex,
            )
        } else {
            Button(onClick = { null }) {
                Text(text = "No account loaded")
            }
        }

        if (activeAccount != null) {
            val senderAddress =
                if (mode == Mode.TRANSPARENT) truncateAddress(activeAccount.address) else "Your spending-key (hidden)"
            Text(
                text = "From: ${activeAccount.alias}, $senderAddress",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(text = "From: No account loaded", style = MaterialTheme.typography.bodyMedium)
        }

        Text(text = "Receiver", style = MaterialTheme.typography.labelMedium)
        // button to scan qr code
        // or pick one of your accounts from a dropdown
        OutlinedTextField(
            value = receiver,
            onValueChange = onReceiverChange,
            label = {
                val label =
                    if (mode == Mode.TRANSPARENT) "Transparent (tnam) address" else "Shielded (znam) address"
                Text(label)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = "Amount", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(text = "Memo", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = memo,
            onValueChange = onMemoChange,
            label = { Text("Required for sending to centralized exchanges") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tx Fee: <fee, denom> <settings button>",
                style = MaterialTheme.typography.labelMedium
            )
            // fee settings button
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            Button(
                onClick = onSubmit,
                enabled = receiver.isNotEmpty() && (amount.toDoubleOrNull()
                    ?: 0.0) in 0.001..balance
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun ShieldProcessingView(transactionResult: String?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (transactionResult) {
            null -> Text(text = "Processing...", style = MaterialTheme.typography.bodyMedium)
            "Approved" -> Text(
                text = "Approved",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )

            "Rejected" -> Text(
                text = "Rejected",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun simulateShieldTransaction(onResult: (String) -> Unit) {
    // Simulate a delay for processing the transaction
    Thread {
        Thread.sleep(3000) // Simulates processing time
        onResult(if ((0..1).random() == 1) "Approved" else "Rejected")
    }.start()
}

@Preview(showBackground = true)
@Composable
fun ShieldScreenPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme(darkTheme = true) {
        Surface {
            ShieldScreen(viewModel, rememberNavController())
        }
    }
}