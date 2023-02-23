package com.example.alldocumentsreaderimagescanner.notepad.model

import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository) : ViewModel() {

    private val selectedNotesIds = ArraySet<Int>()

    /**
     * get all notes from db as pager
     */
    fun getAllNotes(): LiveData<PagingData<NotepadEntity>> {

        val pager = Pager(
            config = PagingConfig(10, enablePlaceholders = false), // 10 notes per page
            pagingSourceFactory = {
                repository.getAllNotes()
            }
        )
        return pager.liveData
    }

    fun deleteSelectedNotes() {
        viewModelScope.launch {
            repository.deleteNotesByIds(selectedNotesIds)
            selectedNotesIds.clear() // clear saved ids
        }
    }

    fun setNoteSelected(isSelected: Boolean, noteId: Int) {
        if (isSelected) {
            selectedNotesIds.add(noteId)
        } else {
            selectedNotesIds.remove(noteId)
        }
    }

    fun getNotesCount(): LiveData<Int> {
        return repository.getNotesCount()
    }

    /**
     * this for save note color we need it later ok :D
     */
    private val noteColor = MutableLiveData("#FFFFFF")


    suspend fun saveNote(noteId: Int?, title: String, noteText: String) {

        if (noteId == null) {
            // save new note
            val createdDate = Date().time
            val newNote = NotepadEntity(
                title = title,
                text = noteText,
                createdDate = createdDate,
                updatedDate = createdDate,
                color = noteColor.value!!
            )
            repository.saveNewNote(newNote)
        } else {
            // if id not null so update the old note

            val oldNote = getNoteById(noteId)
            oldNote.title = title
            oldNote.text = noteText
            oldNote.updatedDate = Date().time
            oldNote.color = noteColor.value!!
            repository.updateNote(oldNote)

        }

    }

    /**
     * get note from repository
     * @param noteId : note id not null
     */
    suspend fun getNoteById(noteId: Int): NotepadEntity {
        return repository.findNoteById(noteId)
    }

    fun saveNoteColor(hexColor: String) {
        this.noteColor.value = hexColor
    }

    fun getNoteColor(): LiveData<String> {
        return this.noteColor
    }
}