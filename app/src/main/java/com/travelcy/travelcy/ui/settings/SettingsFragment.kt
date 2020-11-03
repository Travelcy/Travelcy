package com.travelcy.travelcy.ui.settings

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.travelcy.travelcy.MainActivity
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.R
import com.travelcy.travelcy.workers.ExchangeRateUpdateWorker
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var settingsViewModel: SettingsViewModel

    private val itemTouchHelper by lazy {
        // 1. Note that I am specifying all 4 directions.
        //    Specifying START and END also allows
        //    more organic dragging than just specifying UP and DOWN.
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val adapter = recyclerView.adapter as SettingsCurrenciesAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    // 2. Update the backing model. Custom implementation in
                    //    MainRecyclerViewAdapter. You need to implement
                    //    reordering of the backing model inside the method.
                    // 3. Tell adapter to render the model update.
                    adapter.moveItem(from, to)

                    return true
                }
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    // 4. Code block for horizontal swipe.
                    //    ItemTouchHelper handles horizontal swipe as well, but
                    //    it is not relevant with reordering. Ignoring here.
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    val adapter = recyclerView.adapter as SettingsCurrenciesAdapter
                    adapter.saveSort()
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = Firebase.analytics

        val activity: MainActivity = requireActivity() as MainActivity
        val mainApplication: MainApplication =  activity.application as MainApplication
        settingsViewModel = ViewModelProvider(
            this, SettingsViewModelFactory(
                mainApplication.getCurrencyRepository(),
                mainApplication.getBillRepository(),
                mainApplication.getSettingsRepository()
            )
        ).get(SettingsViewModel::class.java)

        val viewManager = LinearLayoutManager(context)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        settingsViewModel.autoUpdateExchangeRates.observe(viewLifecycleOwner, Observer {
            root.settings_auto_update.isChecked = it

            if (!it) {
                WorkManager.getInstance(mainApplication).cancelUniqueWork(ExchangeRateUpdateWorker.WORK_NAME)
            }
        })

        root.settings_auto_update.setOnCheckedChangeListener { buttonView, isChecked -> settingsViewModel.updateAutoUpdateExchangeRates(isChecked) }

        settingsViewModel.exchangeRatesLastUpdated.observe(viewLifecycleOwner, Observer {
            root.settings_last_updated.text = getString(R.string.settings_last_updated, DateFormat.format("MMMM d HH:mm", it * 1000))
        })

        root.settings_update_now.setOnClickListener { settingsViewModel.refreshCurrencies() }

        settingsViewModel.defaultPerson.observe(viewLifecycleOwner, Observer {
            root.settings_budget_amount.setText(settingsViewModel.formatAmount(it.budget))
        })

        root.settings_budget_amount.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                settingsViewModel.updateBudget(v.text.toString())
            }
            false
        }

        val recyclerView: RecyclerView = root.settings_currency_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        itemTouchHelper.attachToRecyclerView(recyclerView)

        val viewAdapter = SettingsCurrenciesAdapter(itemTouchHelper, settingsViewModel)
        recyclerView.adapter = viewAdapter

        settingsViewModel.currencies.observe(viewLifecycleOwner, Observer {
            val currenciesMutable = it.toMutableList()
            currenciesMutable.sortBy { el -> el.sort }

            viewAdapter.currencies = currenciesMutable
            viewAdapter.notifyDataSetChanged()
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