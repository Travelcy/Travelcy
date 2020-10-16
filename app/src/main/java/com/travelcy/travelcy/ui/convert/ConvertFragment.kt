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
import kotlinx.android.synthetic.main.fragment_convert.*
import kotlinx.android.synthetic.main.fragment_convert.view.*

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

        root.switch_button.setOnClickListener { root ->
            switch_button.setText("pressed")
        }

        return root
    }
}