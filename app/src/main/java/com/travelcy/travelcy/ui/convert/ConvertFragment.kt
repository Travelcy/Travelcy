package com.travelcy.travelcy.ui.convert

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.travelcy.travelcy.R
import kotlinx.android.synthetic.main.fragment_convert.*
import kotlinx.android.synthetic.main.fragment_convert.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.travelcy.travelcy.databinding.FragmentConvertBinding
import com.travelcy.travelcy.model.Currency


class ConvertFragment : Fragment() {

    val USD = Currency("USD",  listOf<Pair<String, Double>>(Pair("EUR", 1.123), Pair("ISK", 120.222)))
    val EUR = Currency("EUR",  listOf<Pair<String, Double>>(Pair("USD", 0.877), Pair("ISK", 140.983)))
    val ISK = Currency("ISK",  listOf<Pair<String, Double>>(Pair("EUR", 0.0014), Pair("USD", 0.0012)))

    public val currencies = listOf<Currency>(USD,EUR,ISK)

    private lateinit var convertViewModel: ConvertViewModel

    private lateinit var binding: FragmentConvertBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        convertViewModel =
                ViewModelProviders.of(this).get(ConvertViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_convert, container, false)

        convertViewModel.current_currencies.observe(this, Observer {

        })

        root.switch_button.setOnClickListener { root ->
            switch_button.setText(convertViewModel.current_currencies.value!![1].name)}

        val binding: FragmentConvertBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_convert,
            container,
            false
        )
        val currNames = listCurrencies()
        var fromAA  = ArrayAdapter<String>(activity as Context, android.R.layout.simple_spinner_item, listCurrencies())
        root.from_spinner.adapter = fromAA

        /*
        binding.switchButton.setOnClickListener{switch()}

        fun changeButtonText(text: String){
            switch_button.setText(text)
    }

         */

        return root
    }
/*
    private fun switch() {
        convertViewModel.switch()
    }
 */
    fun listCurrencies(): MutableList<String> {
        var currencyNames = mutableListOf<String>()
        for (curr in currencies){
            currencyNames.add(curr.name)
        }
    return currencyNames
    }
}