package com.example.mvvm2.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvm2.model.dao.ClasseDao
import com.example.mvvm2.viewmodel.ClasseViewModel

class ClasseViewModelFactory(
    private val classeDao: ClasseDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClasseViewModel::class.java)) {
            return ClasseViewModel(classeDao) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}