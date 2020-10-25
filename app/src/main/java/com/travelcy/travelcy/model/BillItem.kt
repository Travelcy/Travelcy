package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bill_items", foreignKeys = [ForeignKey(entity = Bill::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("billId"),
    onDelete = ForeignKey.CASCADE)],
    indices = [Index("id"), Index("billId")]
)
class BillItem(
    var description: String,
    var amount: Double,
    var quantity: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var billId = 1
}