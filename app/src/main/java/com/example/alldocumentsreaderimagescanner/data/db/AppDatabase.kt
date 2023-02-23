package com.example.alldocumentsreaderimagescanner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.alldocumentsreaderimagescanner.notepad.model.NotepadEntity

@Database(entities = [NotepadEntity::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    // add dao abstract fun

    abstract fun getNotesDao(): NotesDao
}