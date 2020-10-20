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
import kotlinx.android.synthetic.main.fragment_convert.*
import kotlinx.android.synthetic.main.fragment_convert.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.databinding.FragmentConvertBindingImpl
import kotlin.math.absoluteValue


class ConvertFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var convertViewModel: ConvertViewModel

    private lateinit var binding: FragmentConvertBindingImpl

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
        convertViewModel.localCurrency.observe(viewLifecycleOwner, Observer {})
        convertViewModel.foreignCurrency.observe(viewLifecycleOwner, Observer {})
        convertViewModel.fromAmount.observe(viewLifecycleOwner, Observer {})
        convertViewModel.toAmount.observe(viewLifecycleOwner, Observer {
            root.to_amount.setText(it.absoluteValue.toString())
        })

        root.from_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                // TODO: Fix horrible cast
                convertViewModel.updateFromAmount(s.toString().toDouble())
                convertViewModel.updateToAmount()
            }
        })

        convertViewModel.currencies.observe(viewLifecycleOwner, Observer {
            // Update UI
            val fromAA  = ArrayAdapter<String>(activity as Context, android.R.layout.simple_spinner_item, convertViewModel.listFromCurrencies())
            root.from_spinner.adapter = fromAA

            val toAA  = ArrayAdapter<String>(activity as Context, android.R.layout.simple_spinner_item, convertViewModel.listToCurrencies())
            root.to_spinner.adapter = toAA
        })

        root.from_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertViewModel.setLocalCurrency(position)
                //TODO: Make nicer
                convertViewModel.setForeignCurrency(convertViewModel.toIndex)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                convertViewModel.setLocalCurrency(0)
            }
        }

        root.to_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertViewModel.setForeignCurrency(position)
        }
            override fun onNothingSelected(parent: AdapterView<*>) {
                convertViewModel.setForeignCurrency(0)
            }
        }

        root.switch_button.setOnClickListener {
            switch_button.text = convertViewModel.listFromCurrencies()[1]
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