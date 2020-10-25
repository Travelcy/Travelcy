package com.travelcy.travelcy.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.travelcy.travelcy.MainActivity
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.R
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = Firebase.analytics

        val activity: MainActivity = requireActivity() as MainActivity
        val mainApplication: MainApplication =  activity.application as MainApplication
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory(mainApplication.getCurrencyRepository())).get(SettingsViewModel::class.java)

        val viewManager = LinearLayoutManager(this.context)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val recyclerView: RecyclerView = root.settings_currency_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        settingsViewModel.currencies.observe(viewLifecycleOwner, Observer {
            val viewAdapter = SettingsCurrenciesAdapter(context, settingsViewModel)

            recyclerView.adapter = viewAdapter
        })

        return root
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Settings Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "SettingsFragment")
        }
    }
}