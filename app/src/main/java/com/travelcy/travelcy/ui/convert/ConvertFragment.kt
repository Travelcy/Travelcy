package com.travelcy.travelcy.ui.convert

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

        convertViewModel.from_currencies.observe(this, Observer {

        })


        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_convert,
            container,
            false
        )

        root.switch_button.setOnClickListener { root ->
            switch_button.setText(convertViewModel.from_currencies.value!![1])}

        binding.switchButton.setOnClickListener{switch()}

        var fromAA  = ArrayAdapter<String>(activity as Context, android.R.layout.simple_spinner_item, convertViewModel.listFromCurrencies())
        root.from_spinner.adapter = fromAA

        var toAA  = ArrayAdapter<String>(activity as Context, android.R.layout.simple_spinner_item, convertViewModel.listToCurrencies())
        root.to_spinner.adapter = toAA


        root.from_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertViewModel.setSelectedFromCurr(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                convertViewModel.setSelectedFromCurr(0)
            }
        }


        root.to_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertViewModel.setSelectedToCurr(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                convertViewModel.setSelectedToCurr(0)
            }
        }

        return root
    }


    private fun switch(){
        convertViewModel.switch()
    }

    private fun populateToSpinner(){
        //TODO: find out how to repopulate
    }
}