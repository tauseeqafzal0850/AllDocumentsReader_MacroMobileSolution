package com.example.alldocumentsreaderimagescanner.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.alldocumentsreaderimagescanner.data.db.AppDatabase
import com.example.alldocumentsreaderimagescanner.data.db.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    private const val DB_NAME = "notes_db"

    /**
     *  create and provide dao to use it (inject it) in repository
     */
    @Provides
    fun provideDao(@ApplicationContext context: Context): NotesDao {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).addMigrations(MIG_2_3).build().getNotesDao()
    }
    val MIG_2_3 = object: Migration(2,3){
        override fun migrate(db: SupportSQLiteDatabase) {
//            Log.e("notes_db", "migrate: ")
        }
    }
}