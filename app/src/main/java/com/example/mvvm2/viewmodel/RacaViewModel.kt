package com.example.mvvm2.viewmodel

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mvvm2.model.dao.RacaDao
import com.example.mvvm2.model.entity.Raca
import kotlinx.coroutines.launch

class RacaViewModel(private val racaDao: RacaDao) : ViewModel() {

    // Estados observáveis para os campos de busca
    var buscaNome = mutableStateOf("")
        private set

    var buscaHabilidade = mutableStateOf("")
        private set

    // Listas observáveis para exibição na UI
    var listaRacas = mutableStateOf<List<Raca>>(emptyList())
        private set

    var listaRacasPorNome = mutableStateOf<List<Raca>>(emptyList())
        private set

    var listaRacasPorHabilidadeEspecifica = mutableStateOf<List<Raca>>(emptyList())
        private set

    // Funções para atualizar os estados de busca
    fun setBuscaNome(nome: String) {
        buscaNome.value = nome
    }

    fun setBuscaHabilidade(habilidade: String) {
        buscaHabilidade.value = habilidade
    }

    // Método para salvar uma nova raça
    fun salvarRaca(nome: String, habilidadeEspecifica: String) {
        viewModelScope.launch {
            val novaRaca = Raca(nome = nome, habilidadeEspecifica = habilidadeEspecifica)
            racaDao.inserir(novaRaca)
            // Atualiza as listas após a inserção
            buscarTodasAsRacas()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaHabilidade.value.isNotEmpty()) {
                buscarPorHabilidadeEspecifica(buscaHabilidade.value)
            }
        }
    }

    // Método para atualizar uma raça existente
    fun atualizarRaca(raca: Raca) {
        viewModelScope.launch {
            racaDao.atualizar(raca)
            // Atualiza as listas após a atualização
            buscarTodasAsRacas()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaHabilidade.value.isNotEmpty()) {
                buscarPorHabilidadeEspecifica(buscaHabilidade.value)
            }
        }
    }

    // Método para excluir uma raça
    fun excluirRaca(raca: Raca) {
        viewModelScope.launch {
            racaDao.deletar(raca)
            // Reexecuta as buscas após a exclusão ser concluída
            buscarTodasAsRacas()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaHabilidade.value.isNotEmpty()) {
                buscarPorHabilidadeEspecifica(buscaHabilidade.value)
            }
        }
    }

    // Método para buscar todas as raças
    fun buscarTodasAsRacas() {
        viewModelScope.launch {
            listaRacas.value = racaDao.buscarTodos()
        }
    }

    // Método para buscar raças por nome
    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            listaRacasPorNome.value = racaDao.buscarPorNome(nome)
        }
    }

    // Método para buscar raças por habilidade específica
    fun buscarPorHabilidadeEspecifica(habilidade: String) {
        viewModelScope.launch {
            listaRacasPorHabilidadeEspecifica.value = racaDao.buscarPorHabilidadeEspecifica(habilidade)
        }
    }
}
