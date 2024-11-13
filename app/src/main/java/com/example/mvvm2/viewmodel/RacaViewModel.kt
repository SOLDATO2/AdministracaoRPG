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

    //estado observavel de busca
    var buscaNome = mutableStateOf("")
        private set

    var buscaHabilidade = mutableStateOf("")
        private set
    ////////////////

    //listas para mostrar no View
    var listaRacas = mutableStateOf<List<Raca>>(emptyList())
        private set

    var listaRacasPorNome = mutableStateOf<List<Raca>>(emptyList())
        private set

    var listaRacasPorHabilidadeEspecifica = mutableStateOf<List<Raca>>(emptyList())
        private set

    //atualizar estado de busca
    fun setBuscaNome(nome: String) {
        buscaNome.value = nome
    }

    fun setBuscaHabilidade(habilidade: String) {
        buscaHabilidade.value = habilidade
    }

    //salvar raça nova
    fun salvarRaca(nome: String, habilidadeEspecifica: String) {
        viewModelScope.launch {
            val novaRaca = Raca(nome = nome, habilidadeEspecifica = habilidadeEspecifica)
            racaDao.inserir(novaRaca)
            //atualizar listas depois de inserir
            buscarTodasAsRacas()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaHabilidade.value.isNotEmpty()) {
                buscarPorHabilidadeEspecifica(buscaHabilidade.value)
            }
        }
    }

    //update raça
    fun atualizarRaca(raca: Raca) {
        viewModelScope.launch {
            racaDao.atualizar(raca)
            //atualiza as listas depois de atualizar///
            buscarTodasAsRacas()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaHabilidade.value.isNotEmpty()) {
                buscarPorHabilidadeEspecifica(buscaHabilidade.value)
            }
            //////////////////////////////////////////////
        }
    }

    //deletar raça
    fun excluirRaca(raca: Raca) {
        viewModelScope.launch {
            racaDao.deletar(raca)
            //atualiza as listas depois de deletar///
            buscarTodasAsRacas()
            if (buscaNome.value.isNotEmpty()) {
                buscarPorNome(buscaNome.value)
            }
            if (buscaHabilidade.value.isNotEmpty()) {
                buscarPorHabilidadeEspecifica(buscaHabilidade.value)
            }
            //////////////////////////////////////////////
        }
    }


    fun buscarTodasAsRacas() {
        viewModelScope.launch {
            listaRacas.value = racaDao.buscarTodos()
        }
    }

    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            listaRacasPorNome.value = racaDao.buscarPorNome(nome)
        }
    }

    fun buscarPorHabilidadeEspecifica(habilidade: String) {
        viewModelScope.launch {
            listaRacasPorHabilidadeEspecifica.value = racaDao.buscarPorHabilidadeEspecifica(habilidade)
        }
    }
}
