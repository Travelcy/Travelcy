package com.travelcy.travelcy.ui.split

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.Person
import kotlinx.android.synthetic.main.person_item.view.*

class PersonViewHolder(private val constraintLayout: RelativeLayout, private val billItem: BillItem?, private val splitViewModel: SplitViewModel): RecyclerView.ViewHolder(constraintLayout) {

    fun setPerson(person: Person, isSelected: Boolean) {
        setPersonName(person.name)
        setSelected(isSelected)

        constraintLayout.person_name.doAfterTextChanged {
            person.name = it.toString()
            splitViewModel.updatePerson(person)
        }

        constraintLayout.person_selected.setOnCheckedChangeListener { checkbox, isChecked ->
            if (billItem != null) {
                if (isChecked) {
                    splitViewModel.addPersonToBillItem(billItem, person)
                }
                else {
                    splitViewModel.removePersonFromBillItem(billItem, person)
                }
            }
        }
    }

    fun setupAddViewHolder(context: Context?) {
        setHint(context?.getString(R.string.title_person_hint) ?: "")
        val newPerson = Person("")
        constraintLayout.person_name.doAfterTextChanged {
            newPerson.name = it.toString()
        }

        constraintLayout.person_add_button.setOnClickListener {
            if (billItem != null) {
                splitViewModel.addPersonToBillItem(billItem, newPerson)
            }
        }
    }


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

class PersonAdapter(private val context: Context?, private val persons: List<Person>, private val selectedPersons: List<Person>, private val billItem: BillItem?, private val splitViewModel: SplitViewModel) :
    RecyclerView.Adapter<PersonViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PersonViewHolder {
        // create a new view
        val personLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item, parent, false) as RelativeLayout
        // set the view's size, margins, paddings and layout parameters

        return PersonViewHolder(personLayout, billItem, splitViewModel)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        if (position < persons.size) {
            val person  = persons[position]

            val isSelected = selectedPersons.find {
                person.id == it.id
            } != null

            holder.setPerson(person, isSelected)
        }
        else {
            holder.setupAddViewHolder(context)
        }
    }

    // We need to add 1 to account for the add user item at the end
    override fun getItemCount() = persons.size + 1

    companion object {
        const val TAG = "PersonAdapter"
    }
}