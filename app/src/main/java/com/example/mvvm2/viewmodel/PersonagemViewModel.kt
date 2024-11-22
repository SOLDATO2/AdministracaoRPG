package com.example.mvvm2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm2.model.PersonagemComDetalhes
import com.example.mvvm2.model.dao.ClasseDao
import com.example.mvvm2.model.dao.PersonagemDao
import com.example.mvvm2.model.dao.RacaDao
import com.example.mvvm2.model.entity.Classe
import com.example.mvvm2.model.entity.Personagem
import com.example.mvvm2.model.entity.Raca
import kotlinx.coroutines.launch

class PersonagemViewModel(
    private val personagemDao: PersonagemDao,
    private val classeDao: ClasseDao,
    private val racaDao: RacaDao
) : ViewModel() {

    var listaPersonagensComDetalhes = mutableStateOf(listOf<PersonagemComDetalhes>())
        private set

    var resultMessage = mutableStateOf<String?>(null)
        private set

    // Estados para armazenar as listas de classes e raças
    var listaClasses = mutableStateOf<List<Classe>>(emptyList())
        private set

    var listaRacas = mutableStateOf<List<Raca>>(emptyList())
        private set

    // **Estados para os campos de busca**
    var buscaNome = mutableStateOf("")
        private set

    var buscaClasse = mutableStateOf("")
        private set

    var buscaRaca = mutableStateOf("")
        private set

    var buscaNivel = mutableStateOf("")
        private set

    // **Listas para armazenar os resultados das buscas**
    var listaPersonagensPorNome = mutableStateOf<List<PersonagemComDetalhes>>(emptyList())
        private set

    var listaPersonagensPorClasse = mutableStateOf<List<PersonagemComDetalhes>>(emptyList())
        private set

    var listaPersonagensPorRaca = mutableStateOf<List<PersonagemComDetalhes>>(emptyList())
        private set

    var listaPersonagensPorNivel = mutableStateOf<List<PersonagemComDetalhes>>(emptyList())
        private set

    init {
        carregarPersonagens()
        carregarClasses()
        carregarRacas()
    }

    //carrega personagens utilizando a classe auxiliar
    fun carregarPersonagens() {
        viewModelScope.launch {
            val personagens = personagemDao.buscarTodos()
            val personagensComDetalhes = personagens.map { personagem ->
                val classe = classeDao.buscarPorId(personagem.classe_id)
                val raca = racaDao.buscarPorId(personagem.raca_id)
                PersonagemComDetalhes(
                    personagem = personagem,
                    nomeClasse = classe?.nome ?: "Desconhecida",
                    nomeRaca = raca?.nome ?: "Desconhecida"
                )
            }
            listaPersonagensComDetalhes.value = personagensComDetalhes
        }
    }

    private fun carregarClasses() {
        viewModelScope.launch {
            listaClasses.value = classeDao.buscarTodos()
        }
    }

    private fun carregarRacas() {
        viewModelScope.launch {
            listaRacas.value = racaDao.buscarTodos()
        }
    }

    fun setBuscaNome(nome: String) {
        buscaNome.value = nome
    }

    fun setBuscaClasse(classe: String) {
        buscaClasse.value = classe
    }

    fun setBuscaRaca(raca: String) {
        buscaRaca.value = raca
    }

    fun setBuscaNivel(nivel: String) {
        buscaNivel.value = nivel
    }

    // salvar personagem
    fun salvarPersonagem(nome: String, classeId: Int?, racaId: Int?, nivel: Int) {
        if (nome.isBlank() || nivel <= 0 || classeId == null || racaId == null) {
            resultMessage.value = "Preencha todos os campos corretamente!"
            return
        }

        viewModelScope.launch {
            val personagem = Personagem(
                id = 0,
                nome = nome,
                classe_id = classeId,
                raca_id = racaId,
                nivel = nivel
            )

            personagemDao.inserir(personagem)
            carregarPersonagens()
            resultMessage.value = "Personagem salvo com sucesso!"
        }
    }

    //excluir um personagem
    fun excluirPersonagem(personagem: Personagem) {
        viewModelScope.launch {
            personagemDao.deletar(personagem)
            carregarPersonagens()
            // reexecuta as buscas após a exclusão
            reexecutarBuscas()
        }
    }

    // atualizar um personagem
    fun atualizarPersonagem(personagem: Personagem) {
        viewModelScope.launch {
            personagemDao.atualizar(personagem)
            carregarPersonagens()
            // reexecuta as buscas após a atualização
            reexecutarBuscas()
        }
    }

    // reexecutar as buscas
    private fun reexecutarBuscas() {
        if (buscaNome.value.isNotBlank()) {
            buscarPorNome(buscaNome.value)
        }
        if (buscaClasse.value.isNotBlank()) {
            buscarPorClasse(buscaClasse.value)
        }
        if (buscaRaca.value.isNotBlank()) {
            buscarPorRaca(buscaRaca.value)
        }
        if (buscaNivel.value.isNotBlank()) {
            val nivel = buscaNivel.value.toIntOrNull()
            if (nivel != null) {
                buscarPorNivel(nivel)
            }
        }
    }

    //buscar por nome
    fun buscarPorNome(nome: String) {
        viewModelScope.launch {
            if (nome.isBlank()) {
                listaPersonagensPorNome.value = emptyList()
            } else {
                val personagens = personagemDao.buscarPorNome("%$nome%")
                val personagensComDetalhes = personagens.map { personagem ->
                    val classe = classeDao.buscarPorId(personagem.classe_id)
                    val raca = racaDao.buscarPorId(personagem.raca_id)
                    PersonagemComDetalhes(
                        personagem = personagem,
                        nomeClasse = classe?.nome ?: "Desconhecida",
                        nomeRaca = raca?.nome ?: "Desconhecida"
                    )
                }
                listaPersonagensPorNome.value = personagensComDetalhes
            }
        }
    }

    //buscar por classe
    fun buscarPorClasse(nomeClasse: String) {
        viewModelScope.launch {
            if (nomeClasse.isBlank()) {
                listaPersonagensPorClasse.value = emptyList()
            } else {
                val personagens = personagemDao.buscarPorClasse("%$nomeClasse%")
                val personagensComDetalhes = personagens.map { personagem ->
                    val classe = classeDao.buscarPorId(personagem.classe_id)
                    val raca = racaDao.buscarPorId(personagem.raca_id)
                    PersonagemComDetalhes(
                        personagem = personagem,
                        nomeClasse = classe?.nome ?: "Desconhecida",
                        nomeRaca = raca?.nome ?: "Desconhecida"
                    )
                }
                listaPersonagensPorClasse.value = personagensComDetalhes
            }
        }
    }

    //buscar por raça
    fun buscarPorRaca(nomeRaca: String) {
        viewModelScope.launch {
            if (nomeRaca.isBlank()) {
                listaPersonagensPorRaca.value = emptyList()
            } else {
                val personagens = personagemDao.buscarPorRaca("%$nomeRaca%")
                val personagensComDetalhes = personagens.map { personagem ->
                    val classe = classeDao.buscarPorId(personagem.classe_id)
                    val raca = racaDao.buscarPorId(personagem.raca_id)
                    PersonagemComDetalhes(
                        personagem = personagem,
                        nomeClasse = classe?.nome ?: "Desconhecida",
                        nomeRaca = raca?.nome ?: "Desconhecida"
                    )
                }
                listaPersonagensPorRaca.value = personagensComDetalhes
            }
        }
    }

    //buscar por nível
    fun buscarPorNivel(nivel: Int?) {
        viewModelScope.launch {
            if (nivel == null) {
                listaPersonagensPorNivel.value = emptyList()
            } else {
                val personagens = personagemDao.buscarPorNivel(nivel)
                val personagensComDetalhes = personagens.map { personagem ->
                    val classe = classeDao.buscarPorId(personagem.classe_id)
                    val raca = racaDao.buscarPorId(personagem.raca_id)
                    PersonagemComDetalhes(
                        personagem = personagem,
                        nomeClasse = classe?.nome ?: "Desconhecida",
                        nomeRaca = raca?.nome ?: "Desconhecida"
                    )
                }
                listaPersonagensPorNivel.value = personagensComDetalhes
            }
        }
    }
}
