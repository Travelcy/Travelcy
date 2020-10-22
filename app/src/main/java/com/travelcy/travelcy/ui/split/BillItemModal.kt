package com.travelcy.travelcy.ui.split

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.travelcy.travelcy.R
import kotlinx.android.synthetic.main.bill_item_modal.view.*

class BillItemModal(private val billItemId: Int, val splitViewModel: SplitViewModel) : DialogFragment() {
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

            billItemWithPersons.observe(this, Observer {
                if (it != null) {
                    val billItem = it.billItem

                    if (billItemDescription.text.toString() !== billItem.description) {
                        billItemDescription.setText(billItem.description)
                    }

                    billItemDescription.setOnEditorActionListener { v, actionId, event ->
                        if(actionId == EditorInfo.IME_ACTION_DONE){
                            billItem.description = billItemDescription.text.toString()
                            splitViewModel.updateBillItem(billItem)
                            val imm: InputMethodManager = v.context
                                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(v.windowToken, 0)
                            billItemDescription.clearFocus()
                            true
                        } else {
                            false
                        }
                    }

                    if (billItemAmount.text.toString() !== billItem.amount.toString()) {
                        billItemAmount.setText(billItem.amount.toString())
                    }

                    billItemAmount.setOnEditorActionListener { v, actionId, event ->
                        if(actionId == EditorInfo.IME_ACTION_DONE){
                            billItem.amount = billItemAmount.text.toString().toDouble()
                            splitViewModel.updateBillItem(billItem)
                            val imm: InputMethodManager = v.context
                                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(v.windowToken, 0)
                            billItemDescription.clearFocus()
                            true
                        } else {
                            false
                        }
                    }

                    if (billItemQuantity.text.toString() !== billItem.quantity.toString()) {
                        billItemQuantity.setText(billItem.quantity.toString())
                    }

                    billItemQuantity.setOnEditorActionListener { v, actionId, event ->
                        if(actionId == EditorInfo.IME_ACTION_DONE){
                            billItem.quantity = billItemQuantity.text.toString().toInt()
                            splitViewModel.updateBillItem(billItem)
                            val imm: InputMethodManager = v.context
                                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(v.windowToken, 0)
                            billItemDescription.clearFocus()
                            true
                        } else {
                            false
                        }
                    }
                }
            })

            builder.setView(root)
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
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