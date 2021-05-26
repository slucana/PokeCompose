package com.example.myapplication.core.util

import com.example.myapplication.core.Constants
import com.example.myapplication.data.model.PokemonType
import com.example.myapplication.ui.TypeColors
import com.example.myapplication.ui.primaryColor

fun PokemonType.defineColor() {
    color = when (type.name) {
        Constants.Types.BUG -> TypeColors.bug
        Constants.Types.FIRE -> TypeColors.fire
        Constants.Types.NORMAL -> TypeColors.normal
        Constants.Types.ELECTRIC -> TypeColors.electric
        Constants.Types.WATER -> TypeColors.water
        Constants.Types.GRASS -> TypeColors.grass
        Constants.Types.ICE -> TypeColors.ice
        Constants.Types.FIGHTING -> TypeColors.fighting
        Constants.Types.POISON -> TypeColors.poison
        Constants.Types.GROUND -> TypeColors.ground
        Constants.Types.FLYING -> TypeColors.flying
        Constants.Types.PSYCHIC -> TypeColors.psychic
        Constants.Types.ROCK -> TypeColors.rock
        Constants.Types.GHOST -> TypeColors.ghost
        Constants.Types.DRAGON -> TypeColors.dragon
        Constants.Types.DARK -> TypeColors.dark
        Constants.Types.STEEL -> TypeColors.steel
        Constants.Types.FAIRY -> TypeColors.fairy
        else -> primaryColor
    }
}
