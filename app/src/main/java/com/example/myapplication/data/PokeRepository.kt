package com.example.myapplication.data

import com.example.myapplication.data.model.Pokemon
import com.example.myapplication.network.PokeService
import retrofit2.Response

interface PokeRepository {
    suspend fun retrievePokemon(name: String) : Response<Pokemon>
}

class PokeRepositoryImpl(private val pokeService: PokeService): PokeRepository {

    override suspend fun retrievePokemon(name: String) = pokeService.retrievePokemon(name)

}



