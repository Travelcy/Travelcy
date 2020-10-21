package com.travelcy.travelcy.model

import androidx.room.Embedded
import androidx.room.Relation

data class BillWithItems (
    @Embedded val bill: Bill,
    @Relation(parentColumn = "id", entityColumn = "billId")
    val items: List<BillItem>
)