package com.travelcy.travelcy

import com.google.gson.GsonBuilder
import com.travelcy.travelcy.services.currency.CurrencyWebService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class CurrencyWebServiceTest {
    // Mock server to mock the api
    private var mockWebServer = MockWebServer()

    private lateinit var currencyWebService: CurrencyWebService

    @Before
    fun setup() {
        // Gson converts JSON responses to java classes
        val gson = GsonBuilder().setLenient().create()

        mockWebServer.start()

        // Create a retrofit instance with the mock server as url and Gson for convertion
        val retrofit = Retrofit
            .Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        //
        currencyWebService = retrofit.create(CurrencyWebService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testCurrencyWebService() {
        val ratesJson = JSONObject()
            .put("CAD", 0.009546683)
            .put("ISK", 1.0)
            .put("DKK", 0.0457137592)
            .put("SEK", 0.064004914)
            .put("EUR", 0.0061425061)
            .put("NOK", 0.0667217445)
            .put("USD", 0.007245086)
            .put("AUD", 0.0100767813)

        val jsonResponse = JSONObject()
            .put("rates", ratesJson)
            .put("base", "ISK")
            .put("date", "2020-10-09")

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "application/json")
            .setBody(jsonResponse.toString())

        mockWebServer.enqueue(response)
        val currencyResponse = currencyWebService.getCurrencies("ISK").execute()
        val currencyWebServiceResponse = currencyResponse.body()

        Assert.assertEquals("ISK", currencyWebServiceResponse.base)

        Assert.assertEquals("2020-10-09", currencyWebServiceResponse.date)

        Assert.assertEquals(0.007245086, currencyWebServiceResponse.rates.get("USD"))
    }
}