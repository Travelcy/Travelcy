package com.travelcy.travelcy.ui.convert

import android.content.Context
import android.os.Bundle
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

        convertViewModel.currencies.observe(viewLifecycleOwner, Observer {
            // Update UI
            var fromAA  = ArrayAdapter<String>(activity as Context, android.R.layout.simple_spinner_item, convertViewModel.listFromCurrencies())
            root.from_spinner.adapter = fromAA

            var toAA  = ArrayAdapter<String>(activity as Context, android.R.layout.simple_spinner_item, convertViewModel.listToCurrencies())
            root.to_spinner.adapter = toAA
        })

        root.switch_button.setOnClickListener {
            switch_button.text = convertViewModel.listFromCurrencies()[1]
        }
        binding.root.setOnClickListener{switch()}

        root.from_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertViewModel.currencies.value
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                convertViewModel.foreignCurrency.value
            }
        }

        root.to_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //convertViewModel.setSelectedToCurr(position)
        }
            override fun onNothingSelected(parent: AdapterView<*>) {
                //convertViewModel.setSelectedToCurr(0)
            }
        }
        return root
    }

    private fun switch(){
        convertViewModel.switch()
    }

    private fun populateToSpinner() {
        //TODO: find out how to repopulate
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Convert Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "ConvertFragment")
        }
    }
}