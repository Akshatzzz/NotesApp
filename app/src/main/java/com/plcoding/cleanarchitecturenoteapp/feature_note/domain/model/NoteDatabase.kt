package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source.NotesDao

@Database(
    entities = [Note::class],
    version = 1
)
abstract class NoteDatabase : RoomDatabase(){
    abstract val noteDao: NotesDao

    companion object {
        const val DATABASE_NAME = "notes_db"
    }
}