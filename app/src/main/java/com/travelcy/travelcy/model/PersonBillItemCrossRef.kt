package com.travelcy.travelcy.model

import androidx.room.Entity

@Entity(tableName = "person_bill_item", primaryKeys = ["personId", "billItemId"])
data class PersonBillItemCrossRef (
    val personId: Int,
    val billItemId: Int
)