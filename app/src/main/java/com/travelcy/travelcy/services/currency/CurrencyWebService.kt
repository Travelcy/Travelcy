package com.travelcy.travelcy.services.currency

import retrofit2.Call;
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyWebService {
    @GET("latest")
    fun getCurrencies(@Query("base") baseCurrency: String): Call<CurrencyWebServiceResponse>
}