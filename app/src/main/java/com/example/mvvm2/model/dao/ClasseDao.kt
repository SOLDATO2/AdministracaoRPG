package com.example.mvvm2.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mvvm2.model.entity.Classe

@Dao
interface ClasseDao {

    @Insert
    suspend fun inserir(classe: Classe)

    @Query("SELECT * FROM classe")
    suspend fun buscarTodos(): List<Classe>

    @Query("SELECT * FROM classe WHERE nome LIKE :nome")
    suspend fun buscarPorNome(nome: String): List<Classe>

    @Query("SELECT * FROM classe WHERE variante LIKE :variante")
    suspend fun buscarPorVariante(variante: String): List<Classe>

    @Update
    suspend fun atualizar(classe: Classe)

    @Delete
    suspend fun deletar(classe: Classe)
}