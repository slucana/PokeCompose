package com.example.myapplication.data.model

import androidx.compose.ui.graphics.Color
import com.example.myapplication.core.Constants.Types.BUG
import com.example.myapplication.core.Constants.Types.DARK
import com.example.myapplication.core.Constants.Types.DRAGON
import com.example.myapplication.core.Constants.Types.ELECTRIC
import com.example.myapplication.core.Constants.Types.FAIRY
import com.example.myapplication.core.Constants.Types.FIGHTING
import com.example.myapplication.core.Constants.Types.FIRE
import com.example.myapplication.core.Constants.Types.FLYING
import com.example.myapplication.core.Constants.Types.GHOST
import com.example.myapplication.core.Constants.Types.GRASS
import com.example.myapplication.core.Constants.Types.GROUND
import com.example.myapplication.core.Constants.Types.ICE
import com.example.myapplication.core.Constants.Types.NORMAL
import com.example.myapplication.core.Constants.Types.POISON
import com.example.myapplication.core.Constants.Types.PSYCHIC
import com.example.myapplication.core.Constants.Types.ROCK
import com.example.myapplication.core.Constants.Types.STEEL
import com.example.myapplication.core.Constants.Types.WATER
import com.example.myapplication.ui.TypeColors
import com.example.myapplication.ui.primaryColor
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Pokemon(
    val name: String = "",
    @Json(name = "base_experience")
    val baseExperience: Int = 0,
    val moves: List<PokemonMove> = emptyList(),
    val sprites: Sprites = Sprites(),
    val types: List<PokemonType> = emptyList(),
    val stats: List<PokemonStat> = emptyList()
) {
    fun getMoves(amount: Int = 20) = with(moves) { subList(0, if (size > amount) amount else size) }
}

@JsonClass(generateAdapter = true)
data class PokemonMove(val move: NamedAPIResource = NamedAPIResource())

@JsonClass(generateAdapter = true)
data class PokemonType(val type: NamedAPIResource = NamedAPIResource()) {
    @Transient
    var color: Color = primaryColor
}

@JsonClass(generateAdapter = true)
data class PokemonStat(
    @Json(name = "base_stat")
    val baseStat: Int = 0,
    val stat: NamedAPIResource = NamedAPIResource()
)

@JsonClass(generateAdapter = true)
data class NamedAPIResource(val name: String = "")

@JsonClass(generateAdapter = true)
data class Sprites(val other: OtherImages? = null) {
    val url = other?.officialArtwork?.frontDefault
}

@JsonClass(generateAdapter = true)
data class OtherImages(@Json(name = "official-artwork") val officialArtwork: OfficialArtwork)

@JsonClass(generateAdapter = true)
data class OfficialArtwork(@Json(name = "front_default") val frontDefault: String)

