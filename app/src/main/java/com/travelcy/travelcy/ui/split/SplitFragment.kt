package com.travelcy.travelcy.ui.split

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.travelcy.travelcy.R

class Bill {}

class Person(var name: String) {
}

class BillItem(private var bill: Bill) {
var description: String? = null
var quantity: Number = 1
var amount: Double = 0.0
var persons: MutableList<Person> = mutableListOf()
}

class SplitFragment : Fragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var splitViewModel: SplitViewModel
    private var bill: Bill = Bill()
    private lateinit var billItems: List<BillItem>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = Firebase.analytics

        val billItem1 = BillItem(bill)
        billItem1.description = "Cheese Burger"
        billItem1.persons.add(Person("Einar Tryggvi"))
        billItem1.amount = 25.0

        val billItem2 = BillItem(bill)
        billItem2.description = "Buffalo Hot Wings"
        billItem2.persons.add(Person("Karl Asgeir"))
        billItem2.quantity = 2
        billItem2.amount = 18.0

        val billItem3 = BillItem(bill)
        billItem3.description = "Cheese fries"
        billItem3.persons.add(Person("Einar Tryggvi"))
        billItem3.persons.add(Person("Karl Asgeir"))
        billItem3.amount = 8.0

        billItems = listOf(billItem1, billItem2, billItem3)

        splitViewModel =
                ViewModelProviders.of(this).get(SplitViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_split, container, false)
        val billItemsView: LinearLayout = root.findViewById(R.id.bill_items)

        for (billItem in billItems) {
            val billItemView: RelativeLayout =
                inflater.inflate(R.layout.bill_item, null) as RelativeLayout

            billItemView.findViewById<TextView>(R.id.bill_item_persons).text = billItem.persons.joinToString(separator = " / ") { "${it.name}" }
            billItemView.findViewById<TextView>(R.id.bill_item_description).text = billItem.description + " × " + billItem.quantity
            billItemView.findViewById<TextView>(R.id.bill_item_amount).text = billItem.amount.toString()

            billItemView.setOnClickListener { showDialog() }

            billItemsView.addView(billItemView)
        }

        val totalAmountView: TextView = root.findViewById(R.id.bill_total_amount)

        totalAmountView.text = billItems.sumByDouble { it.amount }.toString()

        val fab: FloatingActionButton = root.findViewById(R.id.floating_action_button)

        fab.setOnClickListener { showDialog() }

//        splitViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        return root
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Split Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "SplitFragment")
        }
    }

    fun showDialog() {
        val fragmentManager = activity?.let {
            val newFragment = BillItemModal()

            newFragment.show(it.supportFragmentManager, "billItemDialog")
        }

    }
}