package com.example.finalproject.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalproject.database.entities.RecipeEntity

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipes(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipes ORDER BY id DESC LIMIT 10")
    fun getLatestRecipes(): LiveData<List<RecipeEntity>>

    @Query("DELETE FROM recipes")
    fun clearRecipes()
}
