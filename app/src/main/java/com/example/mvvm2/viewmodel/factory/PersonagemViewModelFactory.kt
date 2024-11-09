package com.example.mvvm2.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvm2.model.dao.PersonagemDao
import com.example.mvvm2.viewmodel.PersonagemViewModel

class PersonagemViewModelFactory(
    private val personagemDao: PersonagemDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonagemViewModel::class.java)) {
            return PersonagemViewModel(personagemDao) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}