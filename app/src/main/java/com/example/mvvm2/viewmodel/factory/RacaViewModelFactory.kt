package com.example.mvvm2.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvm2.model.dao.RacaDao
import com.example.mvvm2.viewmodel.RacaViewModel

class RacaViewModelFactory(
    private val racaDao: RacaDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RacaViewModel::class.java)) {
            return RacaViewModel(racaDao) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}