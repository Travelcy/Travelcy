package com.travelcy.travelcy.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class BillItemWithPersons (
    @Embedded var billItem: BillItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(PersonBillItemCrossRef::class, parentColumn = "billItemId", entityColumn = "personId")
    )
    val persons: List<Person>
)