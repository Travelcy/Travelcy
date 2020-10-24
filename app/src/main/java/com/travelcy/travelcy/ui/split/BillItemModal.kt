package com.travelcy.travelcy.ui.split

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.utils.FormatUtils
import kotlinx.android.synthetic.main.bill_item_modal.view.*

class BillItemModal(billItemId: Int, private val splitViewModel: SplitViewModel) : DialogFragment() {
    private val billItemWithPersons = splitViewModel.getBillItem(billItemId)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { fragmentActivity ->
            // Use the Builder class for convenient dialog construction
            val builder = MaterialAlertDialogBuilder(fragmentActivity)
            val inflater = requireActivity().layoutInflater;
            val root = inflater.inflate(R.layout.bill_item_modal, null)

            val billItemDescription: TextInputEditText = root.bill_item_description
            val billItemAmount: TextInputEditText = root.bill_item_amount
            val billItemQuantity: TextInputEditText = root.bill_item_quantity

            var billItem: BillItem? = null

            billItemWithPersons.observe(this, Observer {
                if (it != null) {
                    billItem = it.billItem

                    if (billItemDescription.text.toString() !== billItem?.description) {
                        billItemDescription.setText(billItem?.description ?: "")
                    }

                    billItemDescription.doAfterTextChanged { text ->  billItem?.description = text.toString()}

                    if (billItemAmount.text.toString() !== billItem?.amount.toString()) {
                        billItemAmount.setText(billItem?.amount.toString() ?: "")
                    }

                    billItemAmount.doAfterTextChanged { text ->  billItem?.amount = FormatUtils.editTextToDouble(text)}


                    if (billItemQuantity.text.toString() !== billItem?.quantity.toString()) {
                        billItemQuantity.setText(billItem?.quantity.toString() ?: "")
                    }

                    billItemQuantity.doAfterTextChanged { text ->  billItem?.quantity = FormatUtils.editTextToInt(text)}
                }
            })

            builder.setView(root)
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        if (billItem != null) {
                            splitViewModel.updateBillItem(billItem as BillItem)
                        }
                    })
                .setNeutralButton(R.string.delete,
                    DialogInterface.OnClickListener { dialog, id ->
                        if (billItemWithPersons.value?.billItem != null) {
                            splitViewModel.deleteBillItem(billItemWithPersons.value?.billItem!!)
                        }
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}