package com.travelcy.travelcy.ui.split

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.Person
import kotlinx.android.synthetic.main.person_item.view.*

class PersonViewHolder(private val constraintLayout: RelativeLayout): RecyclerView.ViewHolder(constraintLayout) {
    fun setPersonName(name: String) {
        constraintLayout.person_name.setText(name)
    }

    fun setSelected(isSelected: Boolean) {
        constraintLayout.person_selected.isChecked = isSelected
    }

    fun setHint(hint: String) {
        constraintLayout.person_name_layout.hint = hint
        constraintLayout.person_selected.visibility = View.GONE
        constraintLayout.person_add_button.visibility = View.VISIBLE
    }
}

class PersonAdapter(private val context: Context?, private val persons: List<Person>, private val selectedPersons: List<Person>) :
    RecyclerView.Adapter<PersonViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PersonViewHolder {
        // create a new view
        val personLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item, parent, false) as RelativeLayout
        // set the view's size, margins, paddings and layout parameters

        return PersonViewHolder(personLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        Log.d(TAG, position.toString())
        if (position < persons.size) {
            val person = persons[position]
            val isSelected = selectedPersons.find {
                person.id == it.id
            } != null
            holder.setPersonName(person.name)
            holder.setSelected(isSelected)
        }
        else {
            holder.setHint(context?.getString(R.string.title_person_hint) ?: "")
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = persons.size + 1

    companion object {
        const val TAG = "PersonAdapter"
    }
}