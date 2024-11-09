package com.example.mvvm2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm2.model.dao.PersonagemDao
import com.example.mvvm2.model.entity.Personagem
import kotlinx.coroutines.launch

class PersonagemViewModel(private val personagemDao: PersonagemDao) : ViewModel() {

    var listaPersonagens = mutableStateOf(listOf<Personagem>())
        private set

    init {
        carregarPersonagens()
    }

    private fun carregarPersonagens() {
        viewModelScope.launch {
            listaPersonagens.value = personagemDao.buscarTodos()
        }
    }

    fun salvarPersonagem(nome: String, classe_id: Int, raca_id: Int, nivel: Int): String {
        if (nome.isBlank() || nivel <= 0) {
            return "Preencha todos os campos corretamente!"
        }

        val personagem = Personagem(id = 0, nome = nome, classe_id = classe_id, raca_id = raca_id, nivel = nivel)

        viewModelScope.launch {
            personagemDao.inserir(personagem)
            carregarPersonagens()
        }

        return "Personagem salvo com sucesso!"
    }

    fun excluirPersonagem(personagem: Personagem) {
        viewModelScope.launch {
            personagemDao.deletar(personagem)
            carregarPersonagens()
        }
    }

    fun atualizarPersonagem(personagem: Personagem) {
        viewModelScope.launch {
            personagemDao.atualizar(personagem)
            carregarPersonagens()
        }
    }

    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            listaPersonagens.value = personagemDao.buscarPorNome(nome)
        }
    }

    fun buscarPorClasse(nomeClasse: String) {
        viewModelScope.launch {
            listaPersonagens.value = personagemDao.buscarPorClasse(nomeClasse)
        }
    }

    fun buscarPorRaca(nomeRaca: String) {
        viewModelScope.launch {
            listaPersonagens.value = personagemDao.buscarPorRaca(nomeRaca)
        }
    }

    fun buscarPorNivel(nivel: Int) {
        viewModelScope.launch {
            listaPersonagens.value = personagemDao.buscarPorNivel(nivel)
        }
    }
}