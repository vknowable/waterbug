package com.example.waterbug.httpclient

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Path

data class TransparentBalanceResponse(
    @Json(name = "tokenAddress") val tokenAddress: String,
    @Json(name = "minDenomAmount") val minDenomAmount: String
)

interface IndexerApi {
    @GET("api/v1/account/{address}")
    suspend fun getTransparentBalances(
        @Path("address") address: String
    ): List<TransparentBalanceResponse>
}