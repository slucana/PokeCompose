package com.example.myapplication.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

const val TIME_OUT = 30 * 1000L // 30 seconds
const val BASE_URL = "https://pokeapi.co/api/v2/"

fun createLoggingInterceptor() = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

fun createClient() = OkHttpClient.Builder()
    .addInterceptor(createLoggingInterceptor())
    .callTimeout(TIME_OUT, TimeUnit.SECONDS)
    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(createClient())
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

inline fun <reified T> createService(): T = retrofit.create(T::class.java)




