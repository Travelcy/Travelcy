package com.travelcy.travelcy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.travelcy.travelcy.model.Currency

@Dao
interface CurrencyDao {
    @Insert(onConflict = REPLACE)
    fun insertAll(currencies: List<Currency>)

    @Query("SELECT * FROM currency where id = :currencyCode")
    fun getCurrency(currencyCode: String): LiveData<Currency>

    @Query("SELECT COUNT(*) FROM currency")
    fun hasCurrencies(): Boolean

    @Query("SELECT * FROM currency")
    fun loadCurrencies(): LiveData<List<Currency>>
}