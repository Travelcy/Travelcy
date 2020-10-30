package com.travelcy.travelcy.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.travelcy.travelcy.database.dao.BillDao
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.*
import java.util.concurrent.Executors

@Database(entities = [Currency::class, Settings::class, Bill::class, BillItem::class, Person::class, PersonBillItemCrossRef::class], version = 1)
abstract class TravelcyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    abstract fun settingsDao(): SettingsDao

    abstract fun billDao(): BillDao

    companion object {
        private val TAG = "TravelcyDatabase"
        private val DB_NAME = "travelcy_database"

        @Volatile
        private var INSTANCE: TravelcyDatabase? = null

        fun close(context: Context) {
            val instance = this.getInstance(context)
            instance.close()
            INSTANCE = null
        }

        fun getInstance(context: Context): TravelcyDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TravelcyDatabase::class.java,
                        DB_NAME
                    )
                        .addCallback(seedInitialData(context))
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        fun seedInitialData(context: Context): Callback {
            return object: Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Executors.newSingleThreadExecutor().execute {
                        val travelcyDatabase = getInstance(context)
                        val settingsDao = travelcyDatabase.settingsDao()
                        if (!settingsDao.hasSettings()) {
                            Log.d(TAG, "No settings in database, setting up initial settings")
                            // Setup initial settings
                            settingsDao.updateSettings(Settings("ISK", "USD"))
                        }

                        val billDao = travelcyDatabase.billDao()
                        if (!billDao.hasBill()) {
                            println("SETUP BILL")
                            Log.d(TAG, "No bill in database, setting up bill")
                            billDao.createBill(Bill())
                        }

                        if (!billDao.hasPersons()) {
                            Log.d(TAG, "No persons in database, adding default person")

                            val defaultPerson = Person("Me")
                            defaultPerson.isDefault = true

                            billDao.addPerson(defaultPerson)
                        }
                    }
                }
            }
        }
    }
}