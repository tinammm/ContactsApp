package com.example.contacts.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String?,
    val isFavourite: Boolean,
    val color: Int
)

fun Contact.getInitials(): String {
    return if (lastName.isNullOrEmpty()) {
        firstName.first().uppercase()
    } else {
        firstName.first().uppercase() + lastName.first().uppercase()
    }
}

fun Contact.getFullName(): String {
    return if (lastName.isNullOrEmpty()) {
        firstName
    } else {
        "$firstName $lastName"
    }
}

@Entity(
    tableName = "phone_numbers",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: Long,
    val number: String,
    val type: PhoneNumberType
)


enum class PhoneNumberType {
    HOME,
    MOBILE,
    WORK,
    MAIN,
    OTHER
}