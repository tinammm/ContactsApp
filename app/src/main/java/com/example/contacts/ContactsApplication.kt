package com.example.contacts

import android.app.Application
import com.example.contacts.data.ContactsDatabase

class ContactsApplication : Application() {
    val database by lazy { ContactsDatabase.getDatabase(this) }
}