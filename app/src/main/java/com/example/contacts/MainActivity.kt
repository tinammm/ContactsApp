package com.example.contacts

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.contacts.databinding.ActivityMainBinding
import com.example.contacts.ui.ContactDialogHelper
import com.example.contacts.ui.ContactsViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel: ContactsViewModel by viewModels {
        ContactsViewModel.Factory(application as ContactsApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)


        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        onBackPressedDispatcher.addCallback(this) {
            if (!navController.popBackStack()) {
                finish()
            }
        }

        binding.fab.setOnClickListener {
            ContactDialogHelper.showCreateContactDialog(this) { firstName, lastName, phoneNumber, type ->
                viewModel.insertContact(firstName, lastName, phoneNumber, type)
            }
        }

        // depending on the destination, change the visibility of the fab button
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.listContactsFragment) {
                binding.fab.show()
            } else {
                binding.fab.hide()
            }
        }

    }

    // Function for implementing navigate back button
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}