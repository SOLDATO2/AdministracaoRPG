package com.example.mvvm2.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mvvm2.model.entity.Raca

@Dao
interface RacaDao {

    @Insert
    suspend fun inserir(raca: Raca)

    @Query("SELECT * FROM raca")
    suspend fun buscarTodos(): List<Raca>

    @Query("SELECT * FROM raca WHERE nome LIKE :nome")
    suspend fun buscarPorNome(nome: String): List<Raca>

    @Query("SELECT * FROM raca WHERE habilidadeEspecifica LIKE :habilidade")
    suspend fun buscarPorHabilidadeEspecifica(habilidade: String): List<Raca>

    @Query("SELECT * FROM raca WHERE id = :id")
    suspend fun buscarPorId(id: Int): Raca?

    @Update
    suspend fun atualizar(raca: Raca)

    @Delete
    suspend fun deletar(raca: Raca)
}