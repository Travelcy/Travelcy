package com.travelcy.travelcy.ui.split

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.R
import kotlinx.android.synthetic.main.bill_item.view.*
import kotlinx.android.synthetic.main.fragment_split.view.*
import kotlinx.android.synthetic.main.labeled_item.view.*

class SplitFragment : Fragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var splitViewModel: SplitViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = Firebase.analytics

        val mainApplication: MainApplication = requireActivity().application as MainApplication
        splitViewModel = ViewModelProvider(this, SplitViewModelFactory(mainApplication.getBillRepository(), mainApplication.getCurrencyRepository())).get(
            SplitViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_split, container, false)
        val billItemsView: LinearLayout = root.bill_items
        val totalAmountPerPersonView: LinearLayout = root.bill_total_amount_per_person

        splitViewModel.billItemsWithPersons.observe(viewLifecycleOwner, Observer { billItemsWithPerson ->
            billItemsView.removeAllViews()

            billItemsWithPerson.forEach {
                val billItem = it.billItem
                val billItemView: RelativeLayout =
                    inflater.inflate(R.layout.bill_item, null) as RelativeLayout
                billItemView.bill_item_persons.text = it.persons.joinToString(separator = " / ") { person -> person.name }
                billItemView.bill_item_description.text =
                    "${billItem.description} × ${billItem.quantity}"
                billItemView.bill_item_amount.text = splitViewModel.formatPrice(billItem.amount)

                billItemView.setOnClickListener { showEditBillItemDialog(billItem.id) }

                billItemsView.addView(billItemView)
            }
        })

        splitViewModel.totalAmountFormatted.observe(viewLifecycleOwner, Observer {
            root.bill_total_amount.text = it
        })

        splitViewModel.totalAmountPerPerson.observe(viewLifecycleOwner, Observer {
            totalAmountPerPersonView.removeAllViews()

            root.total_per_person_title.visibility = if(it.isEmpty()) { View.GONE } else { View.VISIBLE }

            it.forEach { personWithPrice ->
                val person = personWithPrice.person
                val labeledView: RelativeLayout =
                    inflater.inflate(R.layout.labeled_item, null) as RelativeLayout
                labeledView.labeled_item_label.text = person.name
                labeledView.labeled_item_value.text = personWithPrice.getTotalAmount()
                if (person.isDefault && person.budget > 0) {
                    labeledView.budget_remaining.text = personWithPrice.getRemainingFormattedBudget()
                    if (personWithPrice.isOverBudget()) {
                        labeledView.budget_remaining.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOverBudget))
                    }
                    else {
                        labeledView.budget_remaining.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorUnderBudget))
                    }
                    labeledView.budget_wrapper.visibility = View.VISIBLE
                }
                totalAmountPerPersonView.addView(labeledView)
            }
        })

        splitViewModel.tipFormatted.observe(viewLifecycleOwner, Observer {
            root.bill_tip.text = it
        })

        splitViewModel.taxFormatted.observe(viewLifecycleOwner, Observer {
            root.bill_tax.text = it
        })

        root.bill_tip_button.setOnClickListener {
            showEditTipModal()
        }

        root.bill_tax_button.setOnClickListener {
            showEditTaxModal()
        }


        val fab: FloatingActionButton = root.findViewById(R.id.floating_action_button)

        fab.setOnClickListener { showEditBillItemDialog(null) }

        return root
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Split Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "SplitFragment")
        }
    }

    private fun showEditBillItemDialog(billItemId: Int?) {
        Log.d(TAG, "showEditBillItemDialog(billItemId: $id)")

        val fragmentManager = activity?.let {

            val newFragment = BillItemModal(billItemId, splitViewModel)

            newFragment.show(it.supportFragmentManager, "billItemDialog")
        }
    }

    private fun showEditTipModal() {
        val fragmentManager = activity?.let {

            val newFragment = TipModal(splitViewModel)

            newFragment.show(it.supportFragmentManager, "tipDialog")
        }
    }

    private fun showEditTaxModal() {
        val fragmentManager = activity?.let {

            val newFragment = TaxModal(splitViewModel)

            newFragment.show(it.supportFragmentManager, "taxDialog")
        }
    }

    companion object {
        const val TAG = "SplitFragment"
    }
}