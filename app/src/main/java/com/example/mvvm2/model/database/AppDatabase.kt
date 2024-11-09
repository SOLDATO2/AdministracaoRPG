package com.example.mvvm2.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mvvm2.model.dao.ClasseDao
import com.example.mvvm2.model.dao.PersonagemDao
import com.example.mvvm2.model.dao.RacaDao
import com.example.mvvm2.model.entity.Classe
import com.example.mvvm2.model.entity.Personagem
import com.example.mvvm2.model.entity.Raca


@Database(entities = [Personagem::class, Raca::class, Classe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personagemDao(): PersonagemDao
    abstract fun racaDao(): RacaDao
    abstract fun classeDao(): ClasseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dnd_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}