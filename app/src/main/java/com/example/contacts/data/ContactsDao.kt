package com.example.contacts.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ContactsDao {

    @Query("SELECT * FROM contacts ORDER BY firstName DESC")
    fun getAllContacts() : LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContact(id: Long) : Contact

    @Query("SELECT id FROM contacts WHERE firstName = :firstName and lastName = :lastName")
    suspend fun getContactByName(firstName: String, lastName: String) : Long?

    @Query("SELECT * FROM phone_numbers WHERE contactId = :contactId")
    suspend fun getPhoneNumbers(contactId : Long) : List<PhoneNumber>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Insert
    suspend fun insertPhoneNumber(phoneNumber: PhoneNumber)

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Delete
    suspend fun deletePhoneNumber(phoneNumber: PhoneNumber)

}