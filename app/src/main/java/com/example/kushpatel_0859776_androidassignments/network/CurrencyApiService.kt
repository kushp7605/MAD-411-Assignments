package com.example.kushpatel_0859776_androidassignments.network

import android.icu.util.Currency
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("currencies")
    suspend fun getCurrencies(): Map<String, String>

    @GET("convert")
    suspend fun getExchangeRate(
        @Query("from") fromCurrency: String,
        @Query("to") toCurrency: String
    ): ExchangeRateResponse
}