package com.example.waterbug.appstate

import retrofit2.Retrofit

sealed class AppState {
    object Loading : AppState() // Generic loading state for app startup

    data class DataLoaded(
        val accounts: List<Account>,
        val networks: List<Network>,
        val activeAccountIndex: Int,
        val activeNetworkIndex: Int,
        val namadaSdk: String?,
        val retrofit: Retrofit?,
    ) : AppState()

    data class NetworkLoading(
        val lastKnownData: DataLoaded // Preserve last known state while updating the network
    ) : AppState()

    data class NetworkError(
        val lastKnownData: DataLoaded, val errorMessage: String
    ) : AppState()

    data class Error(val message: String) :
        AppState() // Generic error, e.g., for loading stored data
}

enum class Mode {
    TRANSPARENT, SHIELDED, INDETERMINATE,
}

data class Account(
    val alias: String,
    val address: String,
    val defaultPayAddr: String,
    val assets: List<Asset> = listOf(), // TODO: this should always have NAM in it, even if balance is 0
    var activeAssetIndex: Int = 0,
    val estRewards: Double = 0.0,
)

data class Network(
    val name: String = "Custom Network",
    val chainId: String,
    val rpcUrl: String,
    val indexerUrl: String,
    val maspIndexerUrl: String,
)

data class Asset(
    val name: String,
    val address: String,
    val balances: Balance,
)

data class Balance(
    val transparentBalance: Double,
    val shieldedBalance: Double,
)