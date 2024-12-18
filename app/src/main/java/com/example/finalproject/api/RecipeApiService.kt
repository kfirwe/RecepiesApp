package com.example.finalproject.api


import com.example.finalproject.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/complexSearch")
    suspend fun getRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int
    ): Response<RecipeResponse>
}

