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
import kotlinx.android.synthetic.main.tax_modal.view.*
import kotlinx.android.synthetic.main.tip_modal.view.*
import java.lang.Exception

class TaxModal(private val splitViewModel: SplitViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { fragmentActivity ->
            // Use the Builder class for convenient dialog construction
            val builder = MaterialAlertDialogBuilder(fragmentActivity)
            val inflater = requireActivity().layoutInflater;
            val root = inflater.inflate(R.layout.tax_modal, null)

            val taxPercentage: TextInputEditText = root.bill_tax_percentage

            var changedTaxPercentage: Double? = null
            splitViewModel.taxPercentage.observe(this, Observer {
                changedTaxPercentage = it

                if (it != null) {
                    taxPercentage.setText((it * 100).toString())
                }
                else {
                    taxPercentage.setText("")
                }

                taxPercentage.doOnTextChanged { text, start, before, count ->
                    try {
                        changedTaxPercentage = if (text.toString().isNotEmpty()) {
                            text.toString().toDouble() / 100
                        } else {
                            0.0
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
                        if (changedTaxPercentage != null) {
                            splitViewModel.setTaxPercentage(changedTaxPercentage!!)
                        }
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}