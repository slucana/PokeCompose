package com.example.myapplication.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class ScreenState<T>(
    var data: T? = null,
    var isLoading: Boolean = false,
    var error: Throwable? = null
)

class ScreenStateManager<T> {

    private val _screenState = MutableLiveData(ScreenState<T>())
    val screenState: LiveData<ScreenState<T>> = _screenState

    fun loading() {
        _screenState.postValue(
            _screenState.value?.copy(data = null, isLoading = true, error = null)
        )
    }

    fun data(data: T?) {
        _screenState.postValue(
            _screenState.value?.copy(data = data, isLoading = false, error = null)
        )
    }

    fun error(error: Throwable?) {
        _screenState.postValue(
            _screenState.value?.copy(data = null, isLoading = false, error = error)
        )
    }
}




