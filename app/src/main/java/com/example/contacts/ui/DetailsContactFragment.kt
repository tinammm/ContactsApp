package com.example.contacts.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.ContactsApplication
import com.example.contacts.R
import com.example.contacts.data.getFullName
import com.example.contacts.data.getInitials
import com.example.contacts.databinding.FragmentDetailsContactBinding

class DetailsContactFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentDetailsContactBinding
    private val viewModel: ContactsViewModel by activityViewModels {
        ContactsViewModel.Factory(requireActivity().application as ContactsApplication)
    }
    private var favoriteMenuItem: MenuItem? = null


    private val phoneNumberAdapter by lazy {
        PhoneNumberAdapter().apply {
            // launches real contacts app
            onNumberClick = {
                launchContactsApp(it.number)
            }
            // removes number from details screen and database
            onDeleteNumber = {
                viewModel.deletePhoneNumber(it)
                Toast.makeText(requireContext(), "Deleted number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailsContactBinding.inflate(layoutInflater, container, false)
        // setting up details top appbar menu
        val host: MenuHost = requireActivity()
        host.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.phoneNumbersRecyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = phoneNumberAdapter
        }

        // attaching item touch to phone adapter for deleting phone number of swipe
        val swipeToDeleteCallback = SwipeToDeleteCallback(requireContext(), phoneNumberAdapter)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.phoneNumbersRecyclerView)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
        favoriteMenuItem = menu.findItem(R.id.action_favorite)
        viewModel.clickedContact.observe(viewLifecycleOwner) {
            binding.contactName.text = it.getFullName()
            binding.circleImageView.text = it.getInitials()

            binding.circleImageView.background.setTint(
                it.color
            )

            favoriteMenuItem?.setIcon(
                if (it.isFavourite) {
                    R.drawable.round_filled
                } else {
                    R.drawable.round_empty
                }
            )
            // populate phone numbers list
            phoneNumberAdapter.updateList(viewModel.phoneNumbers)

        }

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_edit -> {
                viewModel.clickedContact.value?.let {
                    ContactDialogHelper.showEditDialog(
                        requireContext(),
                        it.firstName,
                        it.lastName
                    ) { firstName, lastName ->
                        val contactCopy = it.copy(
                            firstName = firstName,
                            lastName = lastName
                        )
                        viewModel.editContact(contactCopy)
                    }
                }
                true
            }

            R.id.action_favorite -> {
                viewModel.clickedContact.value?.let { viewModel.updateContactFavourite(it) }
                true
            }

            R.id.action_delete -> {
                ContactDialogHelper.showDeleteAlertDialog(requireContext()) {
                    viewModel.clickedContact.value?.let { viewModel.deleteContact(it) }
                    findNavController().navigateUp()
                }
                true
            }

            else -> false
        }
    }

    // Launches real contacts app with given phone number
    private fun launchContactsApp(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }


}