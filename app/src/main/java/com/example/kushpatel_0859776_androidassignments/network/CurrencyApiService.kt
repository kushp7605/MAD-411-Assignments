package com.example.kushpatel_0859776_androidassignments.network

import com.example.kushpatel_0859776_androidassignments.models.ExchangeRatesResponse
import retrofit2.http.GET



interface CurrencyApiService {
    @GET("currencies.json")
    suspend fun getCurrencies(): Map<String, String>

    @GET("currencies/cad.json")
    suspend fun getExchangeRates(): ExchangeRatesResponse
}