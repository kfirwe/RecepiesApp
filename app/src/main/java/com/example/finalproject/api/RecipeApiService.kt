package com.example.finalproject.api


import com.example.finalproject.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/complexSearch")
    suspend fun getRecipes(
        @Query("query") query: String = "",
        @Query("number") number: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("apiKey") apiKey: String
    ): RecipeResponse

}

