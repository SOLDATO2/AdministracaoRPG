package com.example.mvvm2.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "personagem",
    foreignKeys = [
        ForeignKey(
            entity = Classe::class,
            parentColumns = ["id"],
            childColumns = ["classe_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Raca::class,
            parentColumns = ["id"],
            childColumns = ["raca_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Personagem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val classe_id: Int,
    val raca_id: Int,
    val nivel: Int
)