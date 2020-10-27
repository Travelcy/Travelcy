package com.travelcy.travelcy.ui.split

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.travelcy.travelcy.R
import com.travelcy.travelcy.model.Person
import kotlinx.android.synthetic.main.person_item.view.*

class PersonViewHolder(private val context: Context?, private val adapter: PersonAdapter, private val constraintLayout: RelativeLayout, private val persons: MutableList<Pair<Person, Boolean>>): RecyclerView.ViewHolder(constraintLayout) {
    private val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    private fun insertNewPerson(person: Person) {
        if (person.name.isNotEmpty()) {
            persons.add(Pair(person, true))
            adapter.notifyDataSetChanged()
            adapter.notifyItemInserted(persons.size)
        }
    }

    private fun setupNewPersonView() {
        setHint(context?.getString(R.string.new_person_hint) ?: "")
        constraintLayout.person_selected.visibility = View.GONE
        constraintLayout.person_add_button.visibility = View.VISIBLE
        val newPerson = Person("")
        constraintLayout.person_name.doAfterTextChanged {
            newPerson.name = it.toString()
        }

        constraintLayout.person_name.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                insertNewPerson(newPerson)
                inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0);
                true
            } else {
                false
            }
        }

        constraintLayout.person_add_button.setOnClickListener {
            insertNewPerson(newPerson)
        }
    }

    fun setFromIndex(index: Int) {
        if (index < persons.size) {
            setPerson(index)
        }
        else {
            setupNewPersonView()
        }
    }

    fun setPerson(index: Int) {
        val (person, isSelected)  = persons[index]
        setHint(context?.getString(R.string.person_hint) ?: "")
        setPersonName(person.name)
        setSelected(isSelected)
        constraintLayout.person_selected.visibility = View.VISIBLE
        constraintLayout.person_add_button.visibility = View.GONE

        constraintLayout.person_name.doAfterTextChanged {
            person.name = it.toString()
        }

        constraintLayout.person_selected.setOnCheckedChangeListener { checkbox, isChecked ->
            persons[index] = Pair(person, isChecked)
        }
    }

    private fun setPersonName(name: String) {
        constraintLayout.person_name.setText(name)
    }

    private fun setSelected(isSelected: Boolean) {
        constraintLayout.person_selected.isChecked = isSelected
    }

    private fun setHint(hint: String) {
        constraintLayout.person_name_layout.hint = hint
    }
}

class PersonAdapter(private val context: Context?, private val persons: MutableList<Pair<Person, Boolean>>) :
    RecyclerView.Adapter<PersonViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PersonViewHolder {
        // create a new view
        val personLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item, parent, false) as RelativeLayout
        // set the view's size, margins, paddings and layout parameters

        return PersonViewHolder(context, this, personLayout, persons)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.setFromIndex(position)
    }


    // We need to add 1 to account for the add user item at the end
    override fun getItemCount() = persons.size + 1

    companion object {
        const val TAG = "PersonAdapter"
    }
}