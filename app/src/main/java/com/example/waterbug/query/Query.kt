package com.example.waterbug.query

import android.util.Log
import com.example.waterbug.appstate.Asset
import com.example.waterbug.appstate.Balance
import com.example.waterbug.httpclient.IndexerApi
import com.example.waterbug.utils.parseDenom
import retrofit2.Retrofit

suspend fun updateTransparentBalances(
    retrofit: Retrofit,
    currentAssets: List<Asset>,
    address: String,
    onError: (String) -> Unit,
): List<Asset> {
    val indexerApi = retrofit.create(IndexerApi::class.java)

    return try {
        val transparentBalances = indexerApi.getTransparentBalances(address)
        Log.d("balances", transparentBalances.toString())

        val updatedAssets = currentAssets.toMutableList()

        for (balance in transparentBalances) {
            val existingAssetIndex =
                updatedAssets.indexOfFirst { it.address == balance.tokenAddress }
            if (existingAssetIndex != -1) {
                val existingAsset = updatedAssets[existingAssetIndex]
                updatedAssets[existingAssetIndex] = existingAsset.copy(
                    balances = existingAsset.balances.copy(
                        transparentBalance = parseDenom(balance.minDenomAmount)
                    )
                )
            } else {
                updatedAssets.add(
                    Asset(
                        name = "token", address = balance.tokenAddress, balances = Balance(
                            transparentBalance = parseDenom(balance.minDenomAmount),
                            shieldedBalance = 0.0
                        )
                    )
                )
            }
        }

        for ((index, asset) in updatedAssets.withIndex()) {
            if (transparentBalances.none { it.tokenAddress == asset.address }) {
                updatedAssets[index] = asset.copy(
                    balances = asset.balances.copy(
                        transparentBalance = 0.0
                    )
                )
            }
        }

        updatedAssets
    } catch (e: Exception) {
        Log.e("updateTransparentBalances", "Error fetching balances: ${e.message}", e)
        onError("Could not fetch latest balances. Check network.")
        currentAssets // Return the original list if an error occurs
    }
}
