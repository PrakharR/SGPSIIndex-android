package com.example.sgpsiindex.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.sgpsiindex.model.Response
import com.example.sgpsiindex.model.State
import com.example.sgpsiindex.repository.Repository
import com.example.sgpsiindex.utility.Utility

class ViewModel(private val repository: Repository) : androidx.lifecycle.ViewModel() {

    var response = MediatorLiveData<Response>()
    var state = MutableLiveData<State>()
    var dataType = MutableLiveData<String>(Utility.PSI_TWENTY_FOUR_HOURLY)

    init {
        response.addSource(repository.getData()) {
            response.value = it
        }
    }

    fun refresh(dateTime: String) {
        repository.refresh(dateTime)
    }

}