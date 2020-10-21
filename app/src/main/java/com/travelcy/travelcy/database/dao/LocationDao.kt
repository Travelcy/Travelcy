package com.travelcy.travelcy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.travelcy.travelcy.model.Location

@Dao
interface LocationDao {
    @Insert(onConflict = REPLACE)
    fun updateLocation(location: Location)

    @Query("SELECT * FROM location where id = 1")
    fun getLocation(): Location

    @Query("SELECT country FROM location where id = 1")
    fun getCountry(): String

    @Query("SELECT currencyCode FROM location where id = 1")
    fun getCurrencyCode(): String
}