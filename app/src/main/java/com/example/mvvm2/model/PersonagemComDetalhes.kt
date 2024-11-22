package com.example.mvvm2.model

import com.example.mvvm2.model.entity.Personagem

data class PersonagemComDetalhes(
    val personagem: Personagem,
    val nomeClasse: String,
    val nomeRaca: String
)