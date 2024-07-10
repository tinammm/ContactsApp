package com.example.contacts.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class, PhoneNumber::class], version = 1)
abstract class ContactsDatabase : RoomDatabase() {
    abstract fun contactsDao() : ContactsDao

    companion object {
        @Volatile
        private var INSTANCE: ContactsDatabase ?= null

        fun getDatabase(context: Context): ContactsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactsDatabase::class.java,
                    "notes_database.db"
                ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                    .createFromAsset("database/notes_database.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}