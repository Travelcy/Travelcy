package com.travelcy.travelcy.ui.settings

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

class SettingsCurrenciesViewHolder(private val constraintLayout: RelativeLayout, private val adapter: SettingsCurrenciesAdapter): RecyclerView.ViewHolder(constraintLayout) {
    var size = 0
    var _currency: Currency? = null
    var _position: Int? = null

    fun setCurrency(currency: Currency, position: Int) {
        _currency = currency
        _position = position

        itemView.settings_currency_list_title.text = "${currency.id} (${currency.name})";

        itemView.settings_currency_list_switch.isChecked = currency.enabled
    }

    fun setEnabled(enabled: Boolean) {
        if (_currency != null && _position != null) {
            if (_currency!!.enabled != enabled) {
                _currency!!.enabled = enabled

                adapter.currencies.removeAt(_position!!)

                if (!enabled) {
                    adapter.currencies.add(_currency!!)
                }
                else {
                    adapter.currencies.add(1, _currency!!)
                }

                adapter.saveSort()
            }
        }
    }
}

class SettingsCurrenciesAdapter(
    private val itemTouchHelper: ItemTouchHelper,
    private val settingsViewModel: SettingsViewModel
) :
    RecyclerView.Adapter<SettingsCurrenciesViewHolder>() {
    var currencies: MutableList<Currency> = mutableListOf()

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SettingsCurrenciesViewHolder {
        // create a new view
        val personLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.settings_currency_list_item, parent, false) as RelativeLayout
        // set the view's size, margins, paddings and layout parameters

        val viewHolder = SettingsCurrenciesViewHolder(personLayout, this)

        viewHolder.setIsRecyclable(false);

        viewHolder.itemView.settings_currency_list_handle.setOnTouchListener { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                itemTouchHelper.startDrag(viewHolder)
            }
            return@setOnTouchListener true
        }

        viewHolder.itemView.settings_currency_list_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewHolder.setEnabled(isChecked)
        }

        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SettingsCurrenciesViewHolder, position: Int) {
        holder.size = currencies.size
        holder.setCurrency(currencies[position], position)
    }

    override fun getItemCount() = currencies.size

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