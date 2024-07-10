package com.example.contacts.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.data.Contact
import com.example.contacts.data.getFullName
import com.example.contacts.data.getInitials
import com.example.contacts.databinding.ContactHeadingBinding
import com.example.contacts.databinding.ContactListItemBinding

sealed interface ContactsListItem {
    data class ContactName(val contact: Contact) : ContactsListItem
    data class Header(val header: String) : ContactsListItem
}

class ContactsAdapter(contacts: List<Contact>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // stores list of contacts and headers for adapter to display
    private var contactsList = createFlatMap(contacts)

    // lambda function for navigating to details screen and storing clickedContact in viewModel
    var onContactClick: ((contact: Contact) -> Unit)? = null

    companion object {
        private const val VIEW_TYPE_NAME = 0
        private const val VIEW_TYPE_HEADING = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (contactsList[position]) {
            is ContactsListItem.ContactName -> VIEW_TYPE_NAME
            is ContactsListItem.Header -> VIEW_TYPE_HEADING
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NAME -> {
                ContactsViewHolder(
                    ContactListItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            VIEW_TYPE_HEADING -> {
                HeaderViewHolder(
                    ContactHeadingBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {

            is ContactsViewHolder -> holder.bind(contactsList[position] as ContactsListItem.ContactName)
            is HeaderViewHolder -> {
                holder.bind(contactsList[position] as ContactsListItem.Header)
            }
        }
    }

    //Updates the list of contacts in the adapter.
    fun updateList(items: List<Contact>) {
        val newContacts = createFlatMap(items)
        val callback = ContactsDiffCallback(this.contactsList, newContacts)
        val diffResult = DiffUtil.calculateDiff(callback)
        this.contactsList = newContacts
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ContactsViewHolder(
        private val binding: ContactListItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactsListItem.ContactName) {
            binding.contactName.text = item.contact.getFullName()
            binding.circleImageView.text = item.contact.getInitials()
            binding.circleImageView.background.setTint(
                item.contact.color
            )
            binding.root.setOnClickListener {
                onContactClick?.invoke(item.contact)
            }
        }
    }

    class HeaderViewHolder(private val binding: ContactHeadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactsListItem.Header) {
            binding.heading.text = item.header
        }
    }

    class ContactsDiffCallback(
        private val oldList: List<ContactsListItem>,
        private val newList: List<ContactsListItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            if (oldItem is ContactsListItem.ContactName && newItem is ContactsListItem.ContactName) {
                return oldItem.contact.id == newItem.contact.id
            }

            if (oldItem is ContactsListItem.Header && newItem is ContactsListItem.Header) {
                return oldItem.header == newItem.header
            }
            return false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem == newItem
        }

    }


    // Function that takes a list of Contact objects, groups and sorts them by first name initial
    // Puts favourite contacts first in list
    private fun createFlatMap(contacts: List<Contact>): List<ContactsListItem> {
        val contactsMap = contacts.filter { !it.isFavourite }
            .groupBy { it.firstName.first().uppercase() }.toSortedMap().flatMap { entry ->
                listOf(ContactsListItem.Header(entry.key)) + entry.value.sortedBy { it.firstName }
                    .map {
                        ContactsListItem.ContactName(it)
                    }
            }

        val favoriteContacts =
            contacts.filter { it.isFavourite }.groupBy { "Favourites" }
                .flatMap { entry ->
                    listOf(ContactsListItem.Header(entry.key)) + entry.value.sortedBy { it.firstName }
                        .map { ContactsListItem.ContactName(it) }
                }

        return favoriteContacts + contactsMap
    }
}

