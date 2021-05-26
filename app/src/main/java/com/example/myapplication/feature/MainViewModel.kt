package com.example.myapplication.feature

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.Constants
import com.example.myapplication.core.Constants.Types.BUG
import com.example.myapplication.core.Constants.Types.ELECTRIC
import com.example.myapplication.core.Constants.Types.FIRE
import com.example.myapplication.core.Constants.Types.NORMAL
import com.example.myapplication.core.ScreenStateManager
import com.example.myapplication.core.util.defineColor
import com.example.myapplication.data.PokeRepository
import com.example.myapplication.data.model.Pokemon
import com.example.myapplication.data.model.PokemonType
import com.example.myapplication.ui.TypeColors
import com.example.myapplication.ui.primaryColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(
    private val pokeRepository: PokeRepository,
    private val screenStateManager: ScreenStateManager<Pokemon>
) : ViewModel() {

    val screenState = screenStateManager.screenState

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _color = MutableLiveData(primaryColor)
    val color: LiveData<Color> = _color

    private val _isSearching = MutableLiveData(true)
    val isSearching: LiveData<Boolean> = _isSearching

    private val _imageLoaded = MutableLiveData(false)
    val imageLoaded: LiveData<Boolean> = _imageLoaded

    fun notifyImageLoaded(state: Boolean) {
        _imageLoaded.value = state
    }

    fun toggleSearchState() {
        _isSearching.value?.let { _isSearching.value = !it }
    }

    fun onNameInput(newName: String) {
        _name.value = newName
    }

    fun retrievePokemonDetails(name: String) {
        screenStateManager.loading()
        viewModelScope.launch {
            delay(1000) // TODO just to see if the loading transition works in the view
            pokeRepository.retrievePokemon(name.toLowerCase(Locale.getDefault()).trim()).let {
                if (it.isSuccessful) {
                    it.body()?.let { pokemon ->
                        pokemon.types.let { types ->
                            types.forEach { type -> type.defineColor() }
                            _color.value = types.firstOrNull()?.color
                        }
                        screenStateManager.data(pokemon)
                    }
                } else {
                    screenStateManager.error(Throwable())
                }
            }
        }
    }
}

