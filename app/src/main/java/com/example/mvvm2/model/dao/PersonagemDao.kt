package com.example.mvvm2.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mvvm2.model.entity.Personagem

@Dao
interface PersonagemDao {

    @Insert
    suspend fun inserir(personagem: Personagem)

    @Query("SELECT * FROM personagem")
    suspend fun buscarTodos(): List<Personagem>

    @Query("SELECT * FROM personagem WHERE nome LIKE :nome")
    suspend fun buscarPorNome(nome: String): List<Personagem>

    @Query("""
        SELECT * FROM personagem
        WHERE classe_id IN (SELECT id FROM classe WHERE nome LIKE :nomeClasse)
    """)
    suspend fun buscarPorClasse(nomeClasse: String): List<Personagem>

    @Query("""
        SELECT * FROM personagem
        WHERE raca_id IN (SELECT id FROM raca WHERE nome LIKE :nomeRaca)
    """)
    suspend fun buscarPorRaca(nomeRaca: String): List<Personagem>

    @Query("SELECT * FROM personagem WHERE nivel = :nivel")
    suspend fun buscarPorNivel(nivel: Int): List<Personagem>

    @Update
    suspend fun atualizar(personagem: Personagem)

    @Delete
    suspend fun deletar(personagem: Personagem)
}

