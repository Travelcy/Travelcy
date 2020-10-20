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
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.databinding.FragmentConvertBindingImpl

class ConvertFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var convertViewModel: ConvertViewModel

    private lateinit var binding: FragmentConvertBindingImpl

    private lateinit var foreignCurrenciesAdapter: ArrayAdapter<String>
    private lateinit var localCurrenciesAdapter: ArrayAdapter<String>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = Firebase.analytics

        val mainApplication: MainApplication = requireActivity().application as MainApplication
        convertViewModel = ViewModelProvider(this, ConvertViewModelFactory(mainApplication.getCurrencyRepository())).get(ConvertViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_convert, container, false)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_convert,
            container,
            false
        )

        foreignCurrenciesAdapter  = ArrayAdapter(activity as Context, R.layout.spinner_item, mutableListOf())
        root.from_spinner.adapter = foreignCurrenciesAdapter

        localCurrenciesAdapter  = ArrayAdapter(activity as Context, R.layout.spinner_item, mutableListOf())
        root.to_spinner.adapter = foreignCurrenciesAdapter

        root.from_amount.setText(convertViewModel.formatAmount(convertViewModel.fromAmount.value ?: 1.0))

        root.from_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    convertViewModel.updateFromAmount(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

        convertViewModel.toAmount.observe(viewLifecycleOwner, Observer {
            root.to_amount.setText(convertViewModel.formatAmount(it))
        })

        convertViewModel.currencyIds.observe(viewLifecycleOwner, Observer {
            localCurrenciesAdapter.clear()
            localCurrenciesAdapter.addAll(it)

            val localCurrencyIndex = convertViewModel.positionOfLocalCurrency()

            if (localCurrencyIndex >= 0) {
                root.from_spinner.setSelection(localCurrencyIndex)
            }

            foreignCurrenciesAdapter.clear()
            foreignCurrenciesAdapter.addAll(it)

            val foreignCurrencyindex = convertViewModel.positionOfForeignCurrency()
            if (foreignCurrencyindex >= 0) {
                root.to_spinner.setSelection(foreignCurrencyindex)
            }
        })

        convertViewModel.localCurrency.observe(viewLifecycleOwner, Observer {
            val index = convertViewModel.positionOfLocalCurrency()
            if (index >= 0) {
                root.from_spinner.setSelection(index)
            }
        })

        convertViewModel.foreignCurrency.observe(viewLifecycleOwner, Observer {
            val index = convertViewModel.positionOfForeignCurrency()
            if (index >= 0) {
                root.to_spinner.setSelection(index)
            }
        })

        root.from_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != convertViewModel.positionOfLocalCurrency()) {
                    convertViewModel.setLocalCurrency(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        root.to_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertViewModel.setForeignCurrency(position)
        }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        root.switch_button.setOnClickListener {

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