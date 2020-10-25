package com.travelcy.travelcy.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.Currency
import kotlinx.android.synthetic.main.settings_currency_list_item.view.*

class SettingsCurrenciesViewHolder(private val constraintLayout: RelativeLayout, private val settingsViewModel: SettingsViewModel): RecyclerView.ViewHolder(constraintLayout) {
    fun setCurrency(currency: Currency) {
        constraintLayout.settings_currency_list_title.text = currency.id
    }
}

class SettingsCurrenciesAdapter(private val context: Context?, private val settingsViewModel: SettingsViewModel) :
    RecyclerView.Adapter<SettingsCurrenciesViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SettingsCurrenciesViewHolder {
        // create a new view
        val personLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.settings_currency_list_item, parent, false) as RelativeLayout
        // set the view's size, margins, paddings and layout parameters

        return SettingsCurrenciesViewHolder(personLayout, settingsViewModel)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SettingsCurrenciesViewHolder, position: Int) {
        holder.setCurrency(settingsViewModel.currencies.value!![position])
    }

    // We need to add 1 to account for the add user item at the end
    override fun getItemCount() = settingsViewModel.currencies.value!!.size

    companion object {
        const val TAG = "SettingsCurrenciesAdapter"
    }
}