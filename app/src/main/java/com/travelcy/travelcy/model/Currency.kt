package com.travelcy.travelcy.model

data class Currency constructor(
    val name: String,
    val rates: List<Pair<String, Double>>
)