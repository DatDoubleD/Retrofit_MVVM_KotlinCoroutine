package com.example.noteapp.api

import androidx.room.Delete
import com.example.noteapp.data.Note
import retrofit2.http.*

interface NoteApi {
    //ket hop kotlin coroutin k can tra ve call, trả trực tiếp ve Note
    @GET("/note")
    suspend fun getAllNote(): List<Note>

    @POST("/note")
    suspend fun addNote(@Body note: Note): Note

    @DELETE("/note/{id}")
    suspend fun deleteNote(@Path("id") id: Int)

    @PUT("/note/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body note: Note): Note

}