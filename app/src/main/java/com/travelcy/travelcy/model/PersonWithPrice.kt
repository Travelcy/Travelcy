package com.travelcy.travelcy.model

import com.travelcy.travelcy.utils.FormatUtils

data class PersonWithPrice(val person: Person, val amount: Double, val localCurrency: Currency?, val foreignCurrency: Currency?) {
    private var remainingBudget: Double

    init {
        var localAmount = amount
        if (foreignCurrency != null) {
            localAmount = amount / foreignCurrency.exchangeRate
        }
        remainingBudget = person.budget - localAmount
    }

    fun isOverBudget(): Boolean {
        return remainingBudget < 0.0
    }

    fun getRemainingFormattedBudget(): String {
        val exchangeRate = foreignCurrency?.exchangeRate ?: 1.0
        return FormatUtils.formatPrice(remainingBudget * exchangeRate, localCurrency, foreignCurrency)
    }

    fun getTotalAmount(): String {
        return FormatUtils.formatPrice(amount, localCurrency, foreignCurrency)
    }
}