package com.travelcy.travelcy.services.currency

data class CurrencyWebServiceResponse(var rates: Map<String, Double>, var base: String, var date: String) {}