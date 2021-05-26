package com.example.myapplication.network

import com.example.myapplication.data.model.Pokemon
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeService {

    @GET("pokemon/{name}")
    suspend fun retrievePokemon(@Path("name") name: String): Response<Pokemon>
}