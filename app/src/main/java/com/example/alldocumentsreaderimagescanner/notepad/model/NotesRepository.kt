package com.example.alldocumentsreaderimagescanner.notepad.model

import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.example.alldocumentsreaderimagescanner.data.db.NotesDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepository @Inject constructor(private val dao: NotesDao){

    suspend fun findNoteById(noteId: Int): NotepadEntity {
        return this.dao.findNoteById(noteId)
    }

    suspend fun saveNewNote(newNote: NotepadEntity) {
        dao.insert(newNote)
    }

    suspend fun updateNote(oldNote: NotepadEntity) {
        dao.update(oldNote)
    }

    fun getAllNotes(): PagingSource<Int, NotepadEntity> {
        return dao.findAllNotes()
    }

    suspend fun deleteNotesByIds(notesIds: ArraySet<Int>) {
        notesIds.forEach { noteId ->
            dao.deleteNoteById(noteId)
        }
    }

    fun getNotesCount(): LiveData<Int> {
        return dao.getNotesCount()
    }

}