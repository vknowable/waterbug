package com.example.waterbug.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.Account
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.ui.theme.WaterbugTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    viewModel: AppViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    accountIndex: Int
) {
    val account = viewModel.getAccounts().getOrNull(accountIndex)
    var alias by remember { mutableStateOf(account?.alias ?: "") }

    WaterbugTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Edit Account")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = topBarDelete(viewModel, navController, snackbarHostState, accountIndex)
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
                        text = "Account Details",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = alias,
                        onValueChange = { alias = it },
                        label = { Text("Account name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Network Icon"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = account?.address ?: "(Not available)",
                        onValueChange = { },
//                        label = "Transparent address",
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = account?.defaultPayAddr ?: "(Not available)",
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                                if (account != null) {
                                    viewModel.upsertAccount(
                                        editIndex = accountIndex,
                                        account = account.copy(alias = alias),
                                        snackbarHostState = snackbarHostState
                                    )
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = alias.isNotEmpty() && account != null
                        ) {
                            Text("Save", style = MaterialTheme.typography.labelLarge)
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
    accountIndex: Int
): @Composable (RowScope.() -> Unit) = {
    var showDialog by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    TextButton(onClick = { showDialog = true }) {
        Text("Delete")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = {
                Column {
                    Text("Are you sure you want to delete this account?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yes, I'm sure")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isChecked) {
                            viewModel.deleteAccount(accountIndex, snackbarHostState)
                            navController.popBackStack()
                        }
                        showDialog = false
                    },
                    enabled = isChecked // Enable button only if checkbox is checked
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview
@Composable
fun EditAccountScreenPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme {
        Surface {
            EditAccountScreen(viewModel, rememberNavController(), SnackbarHostState(), 0)
        }
    }
}