package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "persons", foreignKeys = [
    ForeignKey(entity = Bill::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("billId"),
    onDelete = ForeignKey.CASCADE)
])
class Person (
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var billId: Int = 1
}