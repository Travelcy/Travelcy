package com.travelcy.travelcy.ui.split

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.travelcy.travelcy.R

class SplitFragment : Fragment() {

    private lateinit var splitViewModel: SplitViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        splitViewModel =
                ViewModelProviders.of(this).get(SplitViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_split, container, false)
        val textView: TextView = root.findViewById(R.id.text_split)
        splitViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}