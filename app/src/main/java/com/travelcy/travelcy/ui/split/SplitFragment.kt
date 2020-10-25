package com.travelcy.travelcy.ui.split

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.ui.convert.ConvertViewModel
import com.travelcy.travelcy.ui.convert.ConvertViewModelFactory
import java.util.concurrent.Executor

class SplitFragment : Fragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var splitViewModel: SplitViewModel
    private lateinit var executor: Executor

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = Firebase.analytics

        val mainApplication: MainApplication = requireActivity().application as MainApplication
        executor = mainApplication.getExecutor()
        splitViewModel = ViewModelProvider(this, SplitViewModelFactory(mainApplication.getBillRepository(), mainApplication.getCurrencyRepository())).get(
            SplitViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_split, container, false)
        val billItemsView: LinearLayout = root.findViewById(R.id.bill_items)

        splitViewModel.billItemsWithPersons.observe(viewLifecycleOwner, Observer { billItemsWithPerson ->
            billItemsView.removeAllViews()

            billItemsWithPerson.forEach {
                val billItem = it.billItem
                val billItemView: RelativeLayout =
                    inflater.inflate(R.layout.bill_item, null) as RelativeLayout
                billItemView.findViewById<TextView>(R.id.bill_item_persons).text = it.persons.joinToString(separator = " / ") { person -> person.name }
                billItemView.findViewById<TextView>(R.id.bill_item_description).text =
                    "${billItem.description} × ${billItem.quantity}"
                billItemView.findViewById<TextView>(R.id.bill_item_amount).text = splitViewModel.formatPrice(billItem.amount)

                billItemView.setOnClickListener { showEditBillItemDialog(billItem.id) }

                billItemsView.addView(billItemView)
            }
        })

        val totalAmountView: TextView = root.findViewById(R.id.bill_total_amount)

        splitViewModel.totalAmount.observe(viewLifecycleOwner, Observer {
            totalAmountView.text = it
        })

        val fab: FloatingActionButton = root.findViewById(R.id.floating_action_button)

        fab.setOnClickListener { showAddBillItemDialog() }

        return root
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Split Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "SplitFragment")
        }
    }

    fun showAddBillItemDialog() {
        Log.d(TAG, "showAddBillItemDialog")
        val billItem = BillItem("", 0.0, 1)

        executor.execute {
            val id = splitViewModel.addBillItem(billItem)
            Log.d(TAG, "addedBillItem with id $id")
            showEditBillItemDialog(id)
        }
    }

    fun showEditBillItemDialog(billItemId: Int) {
        Log.d(TAG, "showEditBillItemDialog(billItemId: $id)")

        val fragmentManager = activity?.let {

            val newFragment = BillItemModal(billItemId, splitViewModel)

            newFragment.show(it.supportFragmentManager, "billItemDialog")
        }

    }

    companion object {
        const val TAG = "SplitFragment"
    }
}