package com.travelcy.travelcy.ui.convert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.travelcy.travelcy.R

class ConvertFragment : Fragment() {

    private lateinit var convertViewModel: ConvertViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        convertViewModel =
                ViewModelProviders.of(this).get(ConvertViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_convert, container, false)
        val textView: TextView = root.findViewById(R.id.text_convert)
        convertViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}