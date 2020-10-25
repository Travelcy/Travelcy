package com.travelcy.travelcy.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.Currency
import kotlinx.android.synthetic.main.settings_currency_list_item.view.*
import java.util.*

class SettingsCurrenciesViewHolder(private val constraintLayout: RelativeLayout, private val settingsViewModel: SettingsViewModel): RecyclerView.ViewHolder(constraintLayout) {
    fun setCurrency(currency: Currency) {
        constraintLayout.settings_currency_list_title.text = "${currency.id} (${currency.name})";

        constraintLayout.settings_currency_list_switch.isChecked = currency.enabled

        constraintLayout.settings_currency_list_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            currency.enabled = isChecked
            if (!isChecked) {
                currency.sort = Int.MAX_VALUE // Sort disabled to to bottom
            }
            settingsViewModel.updateCurrency(currency)
        }
    }
}

class SettingsCurrenciesAdapter(
    private val itemTouchHelper: ItemTouchHelper,
    private val settingsViewModel: SettingsViewModel,
    private val currencies: MutableList<Currency>
) :
    RecyclerView.Adapter<SettingsCurrenciesViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SettingsCurrenciesViewHolder {
        // create a new view
        val personLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.settings_currency_list_item, parent, false) as RelativeLayout
        // set the view's size, margins, paddings and layout parameters

        val viewHolder = SettingsCurrenciesViewHolder(personLayout, settingsViewModel)

        viewHolder.itemView.settings_currency_list_handle.setOnTouchListener { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                itemTouchHelper.startDrag(viewHolder)
            }
            return@setOnTouchListener true
        }

        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SettingsCurrenciesViewHolder, position: Int) {
        holder.setCurrency(currencies[position])
    }

    override fun getItemCount() = settingsViewModel.currencies.value!!.size

    fun moveItem(from: Int, to: Int) {
        notifyItemMoved(from, to)

        Collections.swap(currencies, from, to)
    }

    fun saveSort() {
        currencies.forEachIndexed { index, currency ->
            currency.sort = index
        }
        settingsViewModel.updateCurrencies(currencies)
    }

    companion object {
        const val TAG = "SettingsCurrenciesAdapter"
    }
}