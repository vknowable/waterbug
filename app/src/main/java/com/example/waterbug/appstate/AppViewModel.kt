package com.example.waterbug.appstate

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterbug.httpclient.RetrofitProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class AppViewModel : ViewModel() {
    private val _appState =
        MutableStateFlow<AppState>(AppState.Loading) // Initialize with a subclass
    val appState: StateFlow<AppState> get() = _appState

    // test functions
    // load some dummy data for testing
    fun loadTestData(): AppState.DataLoaded {
        return testState
    }

    //////////// return different AppState types for testing GUI layouts ////////////////
    fun setLoadingState() {
        _appState.value = AppState.Loading
    }

    fun setDataLoadedState() {
        val storedData = loadTestData()
        _appState.value = AppState.DataLoaded(
            accounts = storedData.accounts,
            networks = storedData.networks,
            activeAccountIndex = storedData.activeAccountIndex,
            activeNetworkIndex = storedData.activeNetworkIndex,
            namadaSdk = null,
            retrofit = null,
        )
    }

    fun setNetworkLoadingState() {
        val storedData = loadTestData()
        _appState.value = AppState.NetworkLoading(
            storedData
        )
    }

    fun setNetworkErrorState() {
        val storedData = loadTestData()
        _appState.value = AppState.NetworkError(
            storedData, "Failed to initialize network: Unable to initialize chain context."
        )
    }

    fun setErrorState() {
        _appState.value = AppState.Error("Error loading stored account data.")
    }
    /////////////////////////////////////////////////////////////////////////////////////

    private suspend fun initializeNamadaSdk(network: Network): String {
        // Simulate initialization delay
        delay(1000) // Wait for 3 seconds
        return network.chainId
    }

    fun initializeApp() {
        viewModelScope.launch {
            _appState.value = AppState.Loading
            try {
//            val storedData = loadStoredData() // Load accounts, balances, etc.
                val storedData = loadTestData()

                // Attempt to initialize the Namada SDK
                try {
                    val activeNetwork = storedData.networks[storedData.activeNetworkIndex]
                    val sdk = initializeNamadaSdk(activeNetwork)
                    val retrofit = RetrofitProvider.getRetrofitInstance(activeNetwork.indexerUrl)
                    _appState.value = AppState.DataLoaded(
                        accounts = storedData.accounts,
                        networks = storedData.networks,
                        activeAccountIndex = storedData.activeAccountIndex,
                        activeNetworkIndex = storedData.activeNetworkIndex,
                        namadaSdk = sdk,
                        retrofit = retrofit,
                    )
                } catch (e: Exception) {
                    // Data loaded, but network initialization failed
                    _appState.value = AppState.NetworkError(
                        storedData, "Failed to initialize network: ${e.message}"
                    )
                }

            } catch (e: Exception) {
                // Loading failed entirely
                _appState.value = AppState.Error("Failed to initialize: ${e.message}")
            }
        }
    }

    fun switchNetwork(newNetworkIndex: Int) {
        viewModelScope.launch {
            val currentState = _appState.value
            if (currentState is AppState.DataLoaded) {
                _appState.value = AppState.NetworkLoading(currentState)
                try {
                    val newNetwork = currentState.networks[newNetworkIndex]
                    val newSdk = initializeNamadaSdk(newNetwork)
                    val newRetrofit = RetrofitProvider.getRetrofitInstance(newNetwork.indexerUrl)
                    _appState.value = AppState.DataLoaded(
                        accounts = currentState.accounts,
                        networks = currentState.networks,
                        activeAccountIndex = currentState.activeAccountIndex,
                        activeNetworkIndex = newNetworkIndex,
                        namadaSdk = newSdk,
                        retrofit = newRetrofit,
                    )
                } catch (e: Exception) {
                    // need to amend currentState with new network
                    _appState.value =
                        AppState.NetworkError(currentState, "Failed to connect: ${e.message}")
                }
            }
        }
    }

    fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    private fun getLastKnownData(): AppState.DataLoaded? {
        return when (val currentState = _appState.value) {
            is AppState.DataLoaded -> currentState
            is AppState.NetworkLoading -> currentState.lastKnownData
            is AppState.NetworkError -> currentState.lastKnownData
            else -> null
        }
    }

    // TODO: there are two updateState functions with different functionality
    // this is confusing and functions should either be consolidated or more clearly separated
    private fun updateState(transform: (AppState.DataLoaded) -> AppState.DataLoaded) {
        when (val currentState = _appState.value) {
            is AppState.DataLoaded -> _appState.value = transform(currentState)
            is AppState.NetworkError -> _appState.value =
                currentState.copy(lastKnownData = transform(currentState.lastKnownData))

            else -> {}
        }
    }

    private fun updateStateWithAccounts(
        accountTransform: (List<Account>, Int) -> List<Account>?
    ) {
        when (val currentState = _appState.value) {
            is AppState.DataLoaded -> {
                val updatedAccounts =
                    accountTransform(currentState.accounts, currentState.activeAccountIndex)
                        ?: return
                _appState.value = currentState.copy(accounts = updatedAccounts)
            }

            is AppState.NetworkError -> {
                val updatedAccounts = accountTransform(
                    currentState.lastKnownData.accounts,
                    currentState.lastKnownData.activeAccountIndex
                ) ?: return
                _appState.value =
                    currentState.copy(lastKnownData = currentState.lastKnownData.copy(accounts = updatedAccounts))
            }

            else -> {}
        }
    }

    fun getRetrofitClient(): Retrofit? {
        return getLastKnownData()?.retrofit
    }

    fun getAccounts(): List<Account> {
        return getLastKnownData()?.accounts ?: listOf()
    }

    fun getActiveAccountIndex(): Int {
        return getLastKnownData()?.activeAccountIndex ?: -1
    }

    fun getNetworks(): List<Network> {
        return getLastKnownData()?.networks ?: listOf()
    }

    fun getActiveNetworkIndex(): Int {
        return getLastKnownData()?.activeNetworkIndex ?: -1
    }

    fun getActiveAccountOrNull(): Account? {
        return getLastKnownData()?.accounts?.getOrNull(getActiveAccountIndex())
    }

    fun getActiveNetworkOrNull(): Network? {
        return getLastKnownData()?.networks?.getOrNull(getActiveNetworkIndex())
    }

    fun getActiveAssetOrNull(): Asset? {
        val account = getActiveAccountOrNull()
        return account?.assets?.getOrNull(account.activeAssetIndex)
    }

    fun setActiveAccountIndex(index: Int) {
        updateState { it.copy(activeAccountIndex = index) }
    }

//    private fun setActiveNetworkIndex(index: Int) {
//        updateState { it.copy(activeNetworkIndex = index) }
//    }

    // TODO: refactor
    fun updateCurrentAssets(updatedAssets: List<Asset>) {
        when (val currentState = _appState.value) {
            is AppState.DataLoaded -> {
                val updatedAccounts = getAccounts().toMutableList()
                if (updatedAccounts.size != 0) {
                    val activeAccountIndex = getActiveAccountIndex()
                    val activeAccount = updatedAccounts[activeAccountIndex]

                    val updatedAccount = activeAccount.copy(assets = updatedAssets)
                    updatedAccounts[activeAccountIndex] = updatedAccount
                    _appState.value = currentState.copy(accounts = updatedAccounts)
                }
            }

            else -> return
        }
    }

    fun setActiveAssetIndex(index: Int) {
        updateStateWithAccounts { accounts, activeAccountIndex ->
            accounts.toMutableList().apply {
                this[activeAccountIndex] = this[activeAccountIndex].copy(activeAssetIndex = index)
            }
        }
    }

    fun upsertAccount(
        editIndex: Int? = null, account: Account, snackbarHostState: SnackbarHostState
    ) {
        val (updatedState, snackbarMsg) = if (editIndex == null) {
            // Add as new account
            getLastKnownData()?.copy(
                accounts = getAccounts() + account
            )?.let { it to "Account added" } ?: return
        } else {
            // Update existing account
            getLastKnownData()?.copy(accounts = getAccounts().mapIndexed { index, existing ->
                if (index == editIndex) account else existing
            })?.let { it to "Account updated" } ?: return
        }

        updateState(
            snackbarHostState = snackbarHostState,
            updatedState = updatedState,
            snackbarMsg = snackbarMsg,
        )
    }

    fun upsertNetwork(
        editIndex: Int? = null, network: Network, snackbarHostState: SnackbarHostState
    ) {
        val (updatedState, snackbarMsg) = if (editIndex == null) {
            // Add as new network
            getLastKnownData()?.copy(
                networks = getNetworks() + network
            )?.let { it to "Network added" } ?: return
        } else {
            // Update existing network
            getLastKnownData()?.copy(networks = getNetworks().mapIndexed { index, existing ->
                if (index == editIndex) network else existing
            })?.let { it to "Network updated" } ?: return
        }

        updateState(
            snackbarHostState = snackbarHostState,
            updatedState = updatedState,
            snackbarMsg = snackbarMsg,
        )
    }

    private fun updateState(
        updatedState: AppState.DataLoaded,
        snackbarHostState: SnackbarHostState,
        snackbarMsg: String,
    ) {
        when (val currentState = _appState.value) {
            is AppState.DataLoaded -> {
                _appState.value = updatedState
            }

            is AppState.NetworkError -> {
                _appState.value = currentState.copy(
                    lastKnownData = updatedState
                )
            }

            else -> return
        }
        showSnackbar(snackbarHostState, snackbarMsg)
    }

    fun deleteNetwork(indexToDelete: Int, snackbarHostState: SnackbarHostState) {
        val updatedIndex =
            calculateUpdatedIndex(indexToDelete, getActiveNetworkIndex(), getNetworks().size)
        val updatedState = getLastKnownData()?.copy(
            networks = getNetworks().filterIndexed { index, _ -> index != indexToDelete },
            activeNetworkIndex = updatedIndex,
        ) ?: return

        updateState(
            snackbarHostState = snackbarHostState,
            updatedState = updatedState,
            snackbarMsg = "Network deleted",
        )
    }

    fun deleteAccount(indexToDelete: Int, snackbarHostState: SnackbarHostState) {
        val updatedIndex =
            calculateUpdatedIndex(indexToDelete, getActiveAccountIndex(), getAccounts().size)
        val updatedState = getLastKnownData()?.copy(
            accounts = getAccounts().filterIndexed { index, _ -> index != indexToDelete },
            activeAccountIndex = updatedIndex,
        ) ?: return

        updateState(
            snackbarHostState = snackbarHostState,
            updatedState = updatedState,
            snackbarMsg = "Account deleted",
        )
    }

    private fun calculateUpdatedIndex(indexToDelete: Int, activeIndex: Int, totalSize: Int): Int {
        return when {
            indexToDelete < activeIndex -> activeIndex - 1
            indexToDelete == activeIndex -> totalSize // Select none by setting out of bounds
            else -> activeIndex
        }
    }

}
