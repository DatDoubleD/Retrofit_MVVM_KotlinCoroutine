package com.example.noteapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    //use https(may be server run error with https)
    // or code this line to androidManiFest: android:usesCleartextTraffic="true"
    private const val BASE_URL = "http://192.168.1.8:8080/"
    private val builder = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    val retrofit = builder.build()
    val apiService: NoteApi = retrofit.create(NoteApi::class.java)
}