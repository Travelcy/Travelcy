package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplitViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Split Fragment"
    }
    val text: LiveData<String> = _text
}