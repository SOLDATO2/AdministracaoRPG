package com.example.mvvm2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.example.mvvm2.model.dao.ClasseDao
import com.example.mvvm2.model.entity.Classe
import kotlinx.coroutines.launch

class ClasseViewModel(private val classeDao: ClasseDao) : ViewModel() {

    // Estados observáveis para os campos de busca
    var buscaNome = mutableStateOf("")
        private set

    var buscaVariante = mutableStateOf("")
        private set

    // Listas observáveis para exibição na UI
    var listaClasses = mutableStateOf<List<Classe>>(emptyList())
        private set

    var listaClassesPorNome = mutableStateOf<List<Classe>>(emptyList())
        private set

    var listaClassesPorVariante = mutableStateOf<List<Classe>>(emptyList())
        private set

    // Funções para atualizar os estados de busca
    fun setBuscaNome(nome: String) {
        buscaNome.value = nome
    }

    fun setBuscaVariante(variante: String) {
        buscaVariante.value = variante
    }

    // Método para salvar uma nova classe
    fun salvarClasse(nome: String, variante: String) {
        viewModelScope.launch {
            val novaClasse = Classe(nome = nome, variante = variante)
            classeDao.inserir(novaClasse)
            // Atualiza as listas após a inserção
            buscarTodasAsClasses()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaVariante.value.isNotEmpty()) {
                buscarPorVariante(buscaVariante.value)
            }
        }
    }

    // Método para atualizar uma classe existente
    fun atualizarClasse(classe: Classe) {
        viewModelScope.launch {
            classeDao.atualizar(classe)
            // Atualiza as listas após a atualização
            buscarTodasAsClasses()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaVariante.value.isNotEmpty()) {
                buscarPorVariante(buscaVariante.value)
            }
        }
    }

    // Método para excluir uma classe
    fun excluirClasse(classe: Classe) {
        viewModelScope.launch {
            classeDao.deletar(classe)
            // Reexecuta as buscas após a exclusão ser concluída
            buscarTodasAsClasses()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaVariante.value.isNotEmpty()) {
                buscarPorVariante(buscaVariante.value)
            }
        }
    }

    // Método para buscar todas as classes
    fun buscarTodasAsClasses() {
        viewModelScope.launch {
            listaClasses.value = classeDao.buscarTodos()
        }
    }

    // Método para buscar classes por nome
    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            listaClassesPorNome.value = classeDao.buscarPorNome(nome)
        }
    }

    // Método para buscar classes por variante
    fun buscarPorVariante(variante: String) {
        viewModelScope.launch {
            listaClassesPorVariante.value = classeDao.buscarPorVariante(variante)
        }
    }
}
