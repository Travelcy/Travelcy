package com.travelcy.travelcy.ui.split

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.travelcy.travelcy.R
import kotlinx.android.synthetic.main.tip_modal.view.*
import java.lang.Exception

class TipModal(private val splitViewModel: SplitViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { fragmentActivity ->
            // Use the Builder class for convenient dialog construction
            val builder = MaterialAlertDialogBuilder(fragmentActivity)
            val inflater = requireActivity().layoutInflater;
            val root = inflater.inflate(R.layout.tip_modal, null)

            val tipPercentageView: TextInputEditText = root.bill_tip_percentage
            val tipAmountView: TextInputEditText = root.bill_tip_amount

            var changedTipPercentage: Double? = null
            var changedTipAmount: Double? = null
            splitViewModel.billTipPercentage.observe(this, Observer {
                changedTipPercentage = it

                if (it != null) {
                    tipPercentageView.setText((it * 100).toString())
                }
                else {
                    tipPercentageView.setText("")
                }

                tipPercentageView.doOnTextChanged { text, start, before, count ->
                    try {
                        if (text.toString().isNotEmpty()) {
                            changedTipPercentage = text.toString().toDouble() / 100
                            tipAmountView.setText("")
                        }
                        else {
                            changedTipPercentage = null
                        }
                    }
                    catch (exception: Exception) {
                        // do nothing
                    }
                }
            })

            splitViewModel.billTipAmount.observe(this, Observer {
                changedTipAmount = it

                tipAmountView.setText(it?.toString() ?: "")

                tipAmountView.doOnTextChanged { text, start, before, count ->
                    try {
                        if (text.toString().isNotEmpty()) {
                            changedTipAmount = text.toString().toDouble()
                            tipPercentageView.setText("")
                        }
                        else {
                            changedTipAmount = null
                        }
                    }
                    catch (exception: Exception) {
                        // do nothing
                    }
                }
            })

            builder.setView(root)
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        if (changedTipPercentage != null) {
                            splitViewModel.setTipPercentage(changedTipPercentage)
                            splitViewModel.setTipAmount(null)
                        }
                        else if (changedTipAmount != null) {
                            splitViewModel.setTipAmount(changedTipAmount)
                            splitViewModel.setTipPercentage(null)
                        }
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}