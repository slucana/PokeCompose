package com.example.myapplication

import com.example.myapplication.core.ScreenStateManager
import com.example.myapplication.data.PokeRepository
import com.example.myapplication.data.PokeRepositoryImpl
import com.example.myapplication.feature.MainViewModel
import com.example.myapplication.network.PokeService
import com.example.myapplication.network.createService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// TODO divide in separate modules, enough for first introductory examples
val appModule = module {

    single {  createService<PokeService>() }
    single<PokeRepository> { PokeRepositoryImpl(get()) }

    viewModel { MainViewModel(get(), screenStateManager = ScreenStateManager()) }
}