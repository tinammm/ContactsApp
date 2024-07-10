package com.example.contacts.ui

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.contacts.ContactsApplication
import com.example.contacts.data.Contact
import com.example.contacts.data.PhoneNumber
import com.example.contacts.data.PhoneNumberType
import com.example.contacts.data.getColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    class Factory(private val application: ContactsApplication) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ContactsViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val contactsDao = application.database.contactsDao()

    // Stores contacts displayed in contact list
    private val _contacts: LiveData<List<Contact>> = contactsDao.getAllContacts()
    val contacts: LiveData<List<Contact>> = _contacts


    // Stores which clicked contact for details screen
    private val _clickedContact = MutableLiveData<Contact>()
    val clickedContact: LiveData<Contact> = _clickedContact

    // used for populating phone number adapter in contact details
    var phoneNumbers: List<PhoneNumber> = emptyList()


    fun setClickedContact(contact: Contact) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _clickedContact.postValue(contact)
                phoneNumbers = contactsDao.getPhoneNumbers(contact.id)
            }
        }
    }

    // Function for inserting new contact and corresponding phone number
    @OptIn(ExperimentalStdlibApi::class)
    fun insertContact(firstName: String, lastName: String, number: String, type: PhoneNumberType) {
        viewModelScope.launch {
            var contactId = contactsDao.getContactByName(firstName, lastName)
            if (contactId == null) {
                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    isFavourite = false,
                    color = getColors().random()
                )
                contactId = contactsDao.insertContact(contact)
            }
            val phoneNumber = PhoneNumber(
                contactId = contactId,
                number = number,
                type = type
            )
            contactsDao.insertPhoneNumber(phoneNumber)
        }
    }

    // Edits clickedContact and updates view model stored data
    fun editContact(contact: Contact) {
        viewModelScope.launch {
            contactsDao.updateContact(contact)
        }
        updateContact(contact)
    }

    // Updates if contact is added to/removed from favourites
    fun updateContactFavourite(contact: Contact) {
        val contactCopy = contact.copy(
            id = contact.id,
            firstName = contact.firstName,
            lastName = contact.lastName,
            isFavourite = !contact.isFavourite,
            color = contact.color
        )

        viewModelScope.launch {
            contactsDao.updateContact(contactCopy)
            _clickedContact.postValue(contactCopy)
        }

    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            contactsDao.deleteContact(contact)
        }

    }

    // Deletes single phone number of a contact
    // Cant be called if contact has only one number
    fun deletePhoneNumber(number: PhoneNumber) {
        viewModelScope.launch {
            contactsDao.deletePhoneNumber(number)
        }
    }

    // Update of clicked Contact
    private fun updateContact(it: Contact) {
        setClickedContact(it)
    }

}