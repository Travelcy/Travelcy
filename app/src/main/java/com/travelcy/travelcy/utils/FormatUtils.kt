package com.travelcy.travelcy.utils

import android.text.Editable
import java.lang.Exception
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

    fun formatPrice(amount: Double, local: com.travelcy.travelcy.model.Currency?, foreign: com.travelcy.travelcy.model.Currency?): String {
        var foreignAmount: Double? = null
        if (foreign != null) {
            foreignAmount = foreign.exchangeRate * amount
        }

        val formattedLocalAmount = FormatUtils.formatCurrency(amount, local?.id)
        val formattedForeignAmount = if (foreignAmount != null) {" / " + formatCurrency(foreignAmount, foreign?.id)} else {""}

        return "$formattedLocalAmount$formattedForeignAmount"
    }

    fun formatCurrency(amount: Double, currencyCode: String?): String {
        if (currencyCode == null) return amount.toString()

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(2)
        format.setCurrency(Currency.getInstance(currencyCode))

        return format.format(amount)
    }

    fun editTextToDouble(text: Editable?): Double {
        return stringToDouble(text?.toString())
    }

    fun editTextToInt(text: Editable?): Int {
        return stringToInt(text?.toString())
    }

    fun stringToDouble(string: String?): Double {
        return try {
            string?.toDouble() ?: 0.0
        } catch (exception: Exception) {
            0.0
        }
    }

    fun stringToInt(string: String?): Int {
        return try {
            string?.toInt() ?: 0
        } catch (exception: Exception) {
            0
        }
    }
}