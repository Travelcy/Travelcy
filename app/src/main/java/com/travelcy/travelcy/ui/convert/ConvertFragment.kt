package com.travelcy.travelcy.ui.convert

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.travelcy.travelcy.R
import kotlinx.android.synthetic.main.fragment_convert.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.travelcy.travelcy.MainActivity
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.databinding.FragmentConvertBinding
import com.travelcy.travelcy.utils.FormatUtils

class ConvertFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var convertViewModel: ConvertViewModel

    private lateinit var binding: FragmentConvertBinding

    private lateinit var foreignCurrenciesAdapter: ArrayAdapter<String>
    private lateinit var localCurrenciesAdapter: ArrayAdapter<String>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        firebaseAnalytics = Firebase.analytics
        val activity: MainActivity = requireActivity() as MainActivity
        val mainApplication: MainApplication =  activity.application as MainApplication
        convertViewModel = ViewModelProvider(this, ConvertViewModelFactory(mainApplication.getCurrencyRepository(), activity.locationRepository)).get(ConvertViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_convert, container, false)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_convert,
            container,
            false
        )

        localCurrenciesAdapter  = ArrayAdapter(activity as Context, R.layout.spinner_item, mutableListOf())
        root.local_spinner.adapter = localCurrenciesAdapter

        foreignCurrenciesAdapter  = ArrayAdapter(activity as Context, R.layout.spinner_item, mutableListOf())
        root.foreign_spinner.adapter = foreignCurrenciesAdapter

        root.foreign_amount.setText(FormatUtils.formatDecimal(convertViewModel.foreignAmount.value ?: 1.0))

        root.foreign_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    convertViewModel.updateForeignAmount(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

        convertViewModel.localAmount.observe(viewLifecycleOwner, Observer {
            root.local_amount.setText(convertViewModel.formatAmount(it))
        })

        convertViewModel.currencyIds.observe(viewLifecycleOwner, Observer {
            localCurrenciesAdapter.clear()
            localCurrenciesAdapter.addAll(it)
            localCurrenciesAdapter.notifyDataSetChanged()

            val localCurrencyIndex = convertViewModel.positionOfLocalCurrency()

            if (localCurrencyIndex >= 0) {
                root.local_spinner.setSelection(localCurrencyIndex)
            }

            foreignCurrenciesAdapter.clear()
            foreignCurrenciesAdapter.addAll(it)
            foreignCurrenciesAdapter.notifyDataSetChanged()

            val foreignCurrencyindex = convertViewModel.positionOfForeignCurrency()
            if (foreignCurrencyindex >= 0) {
                root.foreign_spinner.setSelection(foreignCurrencyindex)
            }
        })

        convertViewModel.localCurrency.observe(viewLifecycleOwner, Observer {
            val index = convertViewModel.positionOfLocalCurrency()
            if (index >= 0) {
                root.local_spinner.setSelection(index)
            }
        })

        convertViewModel.foreignCurrency.observe(viewLifecycleOwner, Observer {
            val index = convertViewModel.positionOfForeignCurrency()
            if (index >= 0) {
                root.foreign_spinner.setSelection(index)
            }
        })

        convertViewModel.networkConnected.observe(viewLifecycleOwner, Observer {
            root.no_network.visibility = if (it) {View.GONE} else {View.VISIBLE}
            root.local_spinner_dropdown_icon.visibility = if (it) {View.VISIBLE} else {View.GONE}
            root.local_spinner.isEnabled = it
        })

        root.local_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != convertViewModel.positionOfLocalCurrency()) {
                    convertViewModel.setLocalCurrency(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        root.foreign_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != convertViewModel.positionOfForeignCurrency()) {
                    convertViewModel.setForeignCurrency(position)
                }
        }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        root.switch_button.setOnClickListener {
            convertViewModel.switch()
        }

        root.geo_location_button.setOnClickListener {
            convertViewModel.updateCurrencyBasedOnLocation()
        }

        binding.root.setOnClickListener{switch()}


        return root
    }

    private fun switch(){
        convertViewModel.switch()
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Convert Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "ConvertFragment")
        }
    }
}