package com.travelcy.travelcy.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Settings


data class SettingsWithCurrencies (
    @Embedded
    private val settings: Settings? = null,
    @Relation(parentColumn = "localCurrencyId", entityColumn = "id")
    val localCurrency: Currency? = null,
    @Relation(parentColumn = "foreignCurrencyId", entityColumn = "id")
    val foreignCurrency: Currency? = null
)