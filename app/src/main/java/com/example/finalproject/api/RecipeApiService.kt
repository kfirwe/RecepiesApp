package com.example.finalproject.api


import com.example.finalproject.data.models.IngredientResponse
import com.example.finalproject.data.models.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/{id}/ingredientWidget.json")
    suspend fun getIngredients(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): IngredientResponse

    @GET("recipes/complexSearch")
    suspend fun getRecipes(
        @Query("query") query: String = "",
        @Query("number") number: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("apiKey") apiKey: String
    ): RecipeResponse

}

