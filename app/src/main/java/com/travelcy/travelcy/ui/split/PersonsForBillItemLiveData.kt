package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.Person

class PersonsForBillItemLiveData (
    private val persons: LiveData<List<Person>>,
    private val selectedPersons: LiveData<List<Person>>
): MediatorLiveData<List<Pair<Person, Boolean>>>() {
    private fun updatePersons(persons: List<Person>?, selectedPersons: List<Person>?) {
        value = persons?.map { person ->
            val selectedPerson = selectedPersons?.find {
                person.id == it.id
            }
            Pair(person, selectedPerson != null)
        } ?: emptyList()
    }


    init {
        addSource(persons) {
            updatePersons(it, selectedPersons.value)
        }

        addSource(selectedPersons) {
            updatePersons(persons.value, it)
        }
    }
}