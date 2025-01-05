package com.example.finalproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finalproject.database.dao.UserDao
import com.example.finalproject.database.entities.UserEntity

import com.example.finalproject.database.dao.RecipeDao
import com.example.finalproject.database.entities.RecipeEntity

@Database(entities = [UserEntity::class, RecipeEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Delete and recreate DB on schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



