package com.example.mvvm2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm2.model.dao.ClasseDao
import com.example.mvvm2.model.entity.Classe
import kotlinx.coroutines.launch


class ClasseViewModel(private val classeDao: ClasseDao) : ViewModel() {

    var listaClasses = mutableStateOf(listOf<Classe>())
        private set

    init {
        carregarClasses()
    }

    private fun carregarClasses() {
        viewModelScope.launch {
            listaClasses.value = classeDao.buscarTodos()
        }
    }

    fun salvarClasse(nome: String, variante: String): String {
        if (nome.isBlank() || variante.isBlank()) {
            return "Preencha todos os campos!"
        }

        val classe = Classe(id = 0, nome = nome, variante = variante)

        viewModelScope.launch {
            classeDao.inserir(classe)
            carregarClasses()
        }

        return "Classe salva com sucesso!"
    }

    fun excluirClasse(classe: Classe) {
        viewModelScope.launch {
            classeDao.deletar(classe)
            carregarClasses()
        }
    }

    fun atualizarClasse(classe: Classe) {
        viewModelScope.launch {
            classeDao.atualizar(classe)
            carregarClasses()
        }
    }

    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            listaClasses.value = classeDao.buscarPorNome(nome)
        }
    }

    fun buscarPorVariante(variante: String) {
        viewModelScope.launch {
            listaClasses.value = classeDao.buscarPorVariante(variante)
        }
    }
}