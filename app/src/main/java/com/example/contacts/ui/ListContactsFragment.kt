package com.example.contacts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.ContactsApplication
import com.example.contacts.R
import com.example.contacts.databinding.FragmentListContactsBinding

class ListContactsFragment : Fragment() {

    private lateinit var binding: FragmentListContactsBinding

    private val viewModel: ContactsViewModel by activityViewModels {
        ContactsViewModel.Factory(requireActivity().application as ContactsApplication)
    }

    private val contactsAdapter by lazy {
        ContactsAdapter(emptyList()).apply {
            // Navigate to details screen
            onContactClick = {
                viewModel.setClickedContact(it)
                findNavController().navigate(R.id.action_listContactsFragment_to_detailsContactFragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewContacts.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }

        viewModel.contacts.observe(viewLifecycleOwner) {
            contactsAdapter.updateList(it)
        }
    }

}