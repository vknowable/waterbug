package com.example.waterbug.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.appstate.AppState
import com.example.waterbug.appstate.AppViewModel
import com.example.waterbug.appstate.Balance
import com.example.waterbug.components.AccountModalSelector
import com.example.waterbug.components.AssetList
import com.example.waterbug.components.BalanceDisplay
import com.example.waterbug.components.BottomBarButtons
import com.example.waterbug.components.ChainId
import com.example.waterbug.components.EpochDisplay
import com.example.waterbug.components.NetworkModalSelector
import com.example.waterbug.components.TopBarActions
import com.example.waterbug.query.updateTransparentBalances
import com.example.waterbug.ui.theme.WaterbugTheme
import kotlinx.coroutines.launch
import uniffi.waterbugrs.initSdk
import uniffi.waterbugrs.queryEpoch

@Composable
fun HomeScreen(
    viewModel: AppViewModel, navController: NavController, snackbarHostState: SnackbarHostState
) {
    when (viewModel.appState.collectAsState().value) {
        is AppState.Loading -> {
            DisplayLoading()
        }

        is AppState.Error -> {
            DisplayError()
        }

        is AppState.NetworkLoading -> {
            Home(
                viewModel,
                navController,
                infoBox = { InfoBox("Switching networks...", isError = false) },
                snackbarHostState = snackbarHostState,
            )
        }

        is AppState.NetworkError -> {
            Home(
                viewModel, navController,
                infoBox = {
                    InfoBox(
                        "Network error occurred. Displayed info may not be up-to-date!",
                        isError = true
                    )
                },
                snackbarHostState = snackbarHostState,
            )
        }

        is AppState.DataLoaded -> {
            Home(viewModel, navController, snackbarHostState = snackbarHostState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    viewModel: AppViewModel,
    navController: NavController,
    infoBox: @Composable (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val chainId = viewModel.getActiveNetworkOrNull()?.chainId ?: "Not connected"
    val activeAccount = viewModel.getActiveAccountOrNull()

    val scannedResult =
        navController.currentBackStackEntry?.savedStateHandle?.get<String>("scannedResult")

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    AccountModalSelector(
                        viewModel.getAccounts(),
                        { viewModel.setActiveAccountIndex(it) },
                        viewModel.getActiveAccountIndex(),
                        navController
                    )
                }
            }, navigationIcon = {
                NetworkModalSelector(
                    viewModel.getNetworks(),
                    { viewModel.switchNetwork(it) },
                    viewModel.getActiveNetworkIndex(),
                    navController
                )
            }, actions = TopBarActions(navController)
            )
        },
        bottomBar = {
            BottomAppBar(
                content = BottomBarButtons(navController = navController),
            )
        },

        ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            infoBox?.invoke()
            Spacer(modifier = Modifier.height(4.dp))
            ChainId(chainId)
            Spacer(modifier = Modifier.height(8.dp))
            EpochDisplay(
                epoch = 45,
                secsToNextEpoch = 100,
                rewardsPerEpoch = activeAccount?.estRewards ?: 0.0
            )
            Spacer(modifier = Modifier.height(24.dp))

            val activeBalance = viewModel.getActiveAssetOrNull()?.balances ?: Balance(
                transparentBalance = 0.0, shieldedBalance = 0.0
            )
            BalanceDisplay(activeBalance) {
                val retrofit = viewModel.getRetrofitClient()
                if (retrofit != null && activeAccount != null) {
                    scope.launch {
                        val updatedAssets = updateTransparentBalances(retrofit = retrofit,
                            currentAssets = activeAccount.assets,
                            address = activeAccount.address,
                            onError = { viewModel.showSnackbar(snackbarHostState, it) })
                        Log.d("updatedAssets", updatedAssets.toString())
                        viewModel.updateCurrentAssets(updatedAssets)
//                        try {
//                            initSdk("https://rpc.campfire.tududes.com", "", "")
//                        } catch (e: Exception) {
//                            println("Init error ${e.message}")
//                        }
//
//                        try {
//                            val epoch = queryEpoch()
//                            println("Epoch: ${epoch.toString()}")
//                        } catch (e:Exception) {
//                            println("Query error ${e.message}")
//                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (activeAccount != null) {
                AssetList(
                    assets = activeAccount.assets,
                    onAssetClick = { viewModel.setActiveAssetIndex(it) },
                    activeAssetIndex = activeAccount.activeAssetIndex,
                )

            } else {
                Button(onClick = { null }) {
                    Text(text = "No account loaded")
                }
            }
        }
    }
}

@Composable
fun DisplayLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun DisplayError(errorMessage: String = "Error loading data") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun InfoBox(message: String, isError: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isError) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
            )
            .padding(16.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenDataLoadedPreview() {
    val viewModel = AppViewModel()
    viewModel.setDataLoadedState()
    WaterbugTheme(darkTheme = true) {
        HomeScreen(
            viewModel = viewModel,
            navController = rememberNavController(),
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    val viewModel = AppViewModel()
    viewModel.setLoadingState()
    HomeScreen(
        viewModel = viewModel,
        navController = rememberNavController(),
        snackbarHostState = SnackbarHostState()
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    val viewModel = AppViewModel()
    viewModel.setErrorState()
    HomeScreen(
        viewModel = viewModel,
        navController = rememberNavController(),
        snackbarHostState = SnackbarHostState()
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNetworkLoadingPreview() {
    val viewModel = AppViewModel()
    viewModel.setNetworkLoadingState()
    HomeScreen(
        viewModel = viewModel,
        navController = rememberNavController(),
        snackbarHostState = SnackbarHostState()
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNetworkErrorPreview() {
    val viewModel = AppViewModel()
    viewModel.setNetworkErrorState()
    HomeScreen(
        viewModel = viewModel,
        navController = rememberNavController(),
        snackbarHostState = SnackbarHostState()
    )
}