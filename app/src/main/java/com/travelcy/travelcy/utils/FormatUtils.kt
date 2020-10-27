package com.travelcy.travelcy.utils

import android.text.Editable
import com.travelcy.travelcy.model.Currency
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency as JavaCurrency

object FormatUtils {
    private const val DECIMAL_FORMAT: String = "#.##"

    fun formatDecimal(amount: Double): String {
        val decimalFormat = DecimalFormat(DECIMAL_FORMAT)
        decimalFormat.roundingMode = RoundingMode.CEILING
        return decimalFormat.format(amount)
    }

    fun formatPrice(foreignAmount: Double, local: Currency?, foreign: Currency?): String {
        var localAmount: Double? = null
        if (foreign != null) {
            localAmount = foreignAmount / foreign.exchangeRate
        }

        val formattedForeignAmount = formatCurrency(foreignAmount, foreign?.id)
        val formattedLocalAmount = if (localAmount != null) {" / " + formatCurrency(localAmount, local?.id)} else {""}

        return "$formattedForeignAmount$formattedLocalAmount"
    }

    fun formatCurrency(amount: Double, currencyCode: String?): String {
        if (currencyCode == null) return amount.toString()

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.minimumFractionDigits = 0
        format.isGroupingUsed = true
        format.currency = JavaCurrency.getInstance(currencyCode)

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