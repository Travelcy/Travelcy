package com.travelcy.travelcy.ui.split

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.utils.FormatUtils
import kotlinx.android.synthetic.main.bill_item_modal.view.*
import androidx.recyclerview.widget.RecyclerView

class BillItemModal(billItemId: Int, private val splitViewModel: SplitViewModel) : DialogFragment() {
    private val billItem = splitViewModel.getBillItem(billItemId)
    private val billItemPersons = splitViewModel.getPersonsForBillItem(billItemId)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { fragmentActivity ->
            // Use the Builder class for convenient dialog construction
            val builder = MaterialAlertDialogBuilder(fragmentActivity)
            val inflater = requireActivity().layoutInflater;
            val root = inflater.inflate(R.layout.bill_item_modal, null)

            val viewManager = LinearLayoutManager(this.context)

            val billItemDescription: TextInputEditText = root.bill_item_description
            val billItemAmount: TextInputEditText = root.bill_item_amount
            val billItemQuantity: TextInputEditText = root.bill_item_quantity
            val recyclerView: RecyclerView = root.bill_item_modal_persons.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
            }

            var changedBillItem: BillItem? = null

            billItem.observe(this, Observer {
                if (it != null) {
                    changedBillItem = it

                    if (billItemDescription.text.toString() !== changedBillItem?.description) {
                        billItemDescription.setText(changedBillItem?.description ?: "")
                    }

                    billItemDescription.doAfterTextChanged { text ->  changedBillItem?.description = text.toString()}

                    if (billItemAmount.text.toString() !== changedBillItem?.amount.toString()) {
                        billItemAmount.setText(changedBillItem?.amount.toString() ?: "")
                    }

                    billItemAmount.doAfterTextChanged { text ->  changedBillItem?.amount = FormatUtils.editTextToDouble(text)}


                    if (billItemQuantity.text.toString() !== changedBillItem?.quantity.toString()) {
                        billItemQuantity.setText(changedBillItem?.quantity.toString() ?: "")
                    }

                    billItemQuantity.doAfterTextChanged { text ->  changedBillItem?.quantity = FormatUtils.editTextToInt(text)}

                }

                billItemPersons.observe(this, Observer { personsPair ->
                    val (persons, selectedPersons) = personsPair

                    val viewAdapter = PersonAdapter(context, persons, selectedPersons, billItem.value, splitViewModel)

                    recyclerView.adapter = viewAdapter
                })
            })

            builder.setView(root)
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        if (changedBillItem != null) {
                            splitViewModel.updateBillItem(changedBillItem as BillItem)
                        }
                    })
                .setNeutralButton(R.string.delete,
                    DialogInterface.OnClickListener { dialog, id ->
                        if (billItem.value != null) {
                            splitViewModel.deleteBillItem(billItem.value!!)
                        }
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}