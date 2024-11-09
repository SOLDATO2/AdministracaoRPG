package com.example.mvvm2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.example.mvvm2.model.dao.RacaDao
import com.example.mvvm2.model.entity.Raca
import kotlinx.coroutines.launch

class RacaViewModel(private val racaDao: RacaDao) : ViewModel() {

    var listaRacas = mutableStateOf(listOf<Raca>())
        private set

    init {
        carregarRacas()
    }

    private fun carregarRacas() {
        viewModelScope.launch {
            listaRacas.value = racaDao.buscarTodos()
        }
    }

    fun salvarRaca(nome: String, habilidadeEspecifica: String): String {
        if (nome.isBlank() || habilidadeEspecifica.isBlank()) {
            return "Preencha todos os campos!"
        }

        val raca = Raca(id = 0, nome = nome, habilidadeEspecifica = habilidadeEspecifica)

        viewModelScope.launch {
            racaDao.inserir(raca)
            carregarRacas()
        }

        return "Raça salva com sucesso!"
    }

    fun excluirRaca(raca: Raca) {
        viewModelScope.launch {
            racaDao.deletar(raca)
            carregarRacas()
        }
    }

    fun atualizarRaca(raca: Raca) {
        viewModelScope.launch {
            racaDao.atualizar(raca)
            carregarRacas()
        }
    }

    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            listaRacas.value = racaDao.buscarPorNome(nome)
        }
    }

    fun buscarPorHabilidadeEspecifica(habilidade: String) {
        viewModelScope.launch {
            listaRacas.value = racaDao.buscarPorHabilidadeEspecifica(habilidade)
        }
    }
}
