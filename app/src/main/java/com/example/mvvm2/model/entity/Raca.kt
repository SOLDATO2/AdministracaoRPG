package com.example.mvvm2.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "raca")
data class Raca(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val habilidadeEspecifica: String
)