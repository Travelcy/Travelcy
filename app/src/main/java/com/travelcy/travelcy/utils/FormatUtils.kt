package com.travelcy.travelcy.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object FormatUtils {
    private const val DECIMAL_FORMAT: String = "#.##"

    fun formatDecimal(amount: Double): String {
        val decimalFormat = DecimalFormat(DECIMAL_FORMAT)
        decimalFormat.roundingMode = RoundingMode.CEILING
        return decimalFormat.format(amount)
    }

    fun formatCurrency(amount: Double, currencyCode: String?): String {
        if (currencyCode == null) return amount.toString()

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(2)
        format.setCurrency(Currency.getInstance(currencyCode))

        return format.format(amount)
    }
}