package com.example.noteapp.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.noteapp.api.ApiConfig
import com.example.noteapp.database.NoteDatabase
import com.example.noteapp.database.dao.NoteDao
import com.example.noteapp.data.Note

class NoteRepository(app: Application) {
    //NoteRepository là class trung gian xử lý API giữa UI và DATA
    private val noteDao: NoteDao

    init {
        val noteDatabase: NoteDatabase = NoteDatabase.getInstance(app)
        noteDao = noteDatabase.getNoteDao()
    }

    //room
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    fun getAllNote(): LiveData<List<Note>> = noteDao.getAllNote()

    suspend fun insertAllNoteToDatabase(notes: List<Note>) = noteDao.insertAllNote(notes)
    suspend fun deleteAllNoteFromDatabase() = noteDao.deleteAllNote()
    //retrofit
    suspend fun getNotesFromApi() = ApiConfig.apiService.getAllNote()
    suspend fun addNoteToServer(note: Note) = ApiConfig.apiService.addNote(note)
    suspend fun deleteNoteToServer(id: Int) = ApiConfig.apiService.deleteNote(id)
    suspend fun updateNoteToServer(id: Int, note: Note) = ApiConfig.apiService.updateNote(id, note)


}